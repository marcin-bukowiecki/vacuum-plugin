/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.utils

import com.goide.GoParserDefinition
import com.goide.psi.GoAssignmentStatement
import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoBlock
import com.goide.psi.GoCallExpr
import com.goide.psi.GoConditionalExpr
import com.goide.psi.GoElseStatement
import com.goide.psi.GoExprSwitchStatement
import com.goide.psi.GoExpression
import com.goide.psi.GoFile
import com.goide.psi.GoForStatement
import com.goide.psi.GoFunctionDeclaration
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.goide.psi.GoIfStatement
import com.goide.psi.GoImportSpec
import com.goide.psi.GoParenthesesExpr
import com.goide.psi.GoSimpleStatement
import com.goide.psi.GoStatement
import com.goide.psi.GoSwitchStatement
import com.goide.psi.GoTypeDeclaration
import com.goide.psi.GoUnaryExpr
import com.goide.psi.impl.GoElementFactory
import com.goide.psi.impl.GoTypeUtil
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.elementType
import com.intellij.psi.util.nextLeafs

/**
 * @author Marcin Bukowiecki
 */
object VacuumPsiUtils {

    fun allStrings(args: List<GoExpression>): Boolean {
        return GoTypeUtil.getTypesOfExpressions(args).stream().allMatch { i -> GoTypeUtil.isString(i, null) }
    }

    fun updateBinaryExpr(binaryExpr: GoBinaryExpr, left: PsiElement, right: PsiElement): GoBinaryExpr {
        val firstChild = binaryExpr.firstChild
        firstChild.replace(left)
        val lastChild = binaryExpr.lastChild
        if (lastChild == firstChild) return binaryExpr
        lastChild.replace(right)
        return binaryExpr
    }

    fun isImported(context: PsiElement, path: String): Boolean {
        return findImport(context, path) != null
    }

    fun findImport(context: PsiElement, path: String): GoImportSpec? {
        val containingFile = context.containingFile as? GoFile ?: return null
        return containingFile.imports.firstOrNull {
            if (it.alias == null) {
                it.path == path
            } else {
                it.alias == path && it.path == path
            }
        }
    }

    fun createStruct(name: String, context: PsiElement): GoTypeDeclaration {
        val dummyFile = GoElementFactory.createFileFromText(
            context.project, """
             package foo
                        
             type $name struct {
             
             }
             """.trimIndent()
        )

        return dummyFile.children.filterIsInstance<GoTypeDeclaration>().first()
    }

    fun isCall(stmt: GoSimpleStatement, expected: String): Boolean {
        stmt.leftHandExprList?.let { list ->
            (list.firstChild as? GoCallExpr)?.let { callExpr ->
                return callExpr.expression.lastChild?.text == expected
            }
        }

        return false
    }

    fun isTypeOf(stmt: GoSimpleStatement, expected: String): Boolean {
        stmt.leftHandExprList?.let { list ->
            (list.firstChild as? GoCallExpr)?.let { callExpr ->
                (callExpr.expression.firstChild as? GoExpression)?.let { ref ->
                    val refTypes = GoTypeUtil.getTypesOfExpressions(listOf(ref))
                    if (refTypes.size == 1 && refTypes[0] != null && refTypes[0].text == expected) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun getLastStatement(block: GoBlock): GoStatement? {
        return block.children.reversed().firstOrNull { ch -> ch is GoStatement } as? GoStatement
    }

    /**
     * Create smart pointer for given [PsiElement]
     */
    fun <T : PsiElement> T.toSmartPointer(): SmartPsiElementPointer<T> {
        return SmartPointerManager.createPointer(this)
    }

    /**
     * Check if given expression is from right side
     */
    fun isRight(binaryExpr: GoBinaryExpr, given: PsiElement): Boolean {
        if (binaryExpr.right == given) return true
        var rightExpr: GoExpression? = binaryExpr.right
        while (rightExpr != null) {
            if (rightExpr == given) return true
            if (rightExpr is GoParenthesesExpr) {
                rightExpr = rightExpr.unwrapParentheses()
            } else {
                return false
            }
        }
        return false
    }

    /**
     * Extract right expression from binary expression
     */
    fun getRightExpr(binaryExpr: GoBinaryExpr): PsiElement? {
        if (binaryExpr.right !is GoParenthesesExpr) return binaryExpr.right
        var leftExpr: GoExpression? = binaryExpr.right
        while (leftExpr != null) {
            if (leftExpr is GoParenthesesExpr) {
                leftExpr = leftExpr.unwrapParentheses()
            } else {
                return leftExpr
            }
        }
        return null
    }

    /**
     * Extract left expression from binary expression
     */
    fun getLeftExpr(binaryExpr: GoBinaryExpr): PsiElement? {
        if (binaryExpr.left !is GoParenthesesExpr) return binaryExpr.left
        var leftExpr: GoExpression? = binaryExpr.left
        while (leftExpr != null) {
            if (leftExpr is GoParenthesesExpr) {
                leftExpr = leftExpr.unwrapParentheses()
            } else {
                return leftExpr
            }
        }
        return null
    }

    /**
     * Tries to find given parent type
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : PsiElement> findParent(given: PsiElement, toFind: Class<out PsiElement>): T? {
        if (toFind.isInstance(given)) return given as T
        var parent = given.parent
        while (parent != null) {
            if (toFind.isInstance(parent)) return parent as T
            parent = parent.parent
        }
        return parent
    }

    fun unitTestExists(goFunctionDeclaration: GoFunctionOrMethodDeclaration?): Boolean {
        return getUnitTestFunction(goFunctionDeclaration) != null
    }

    fun getUnitTestFunction(goFunctionDeclaration: GoFunctionOrMethodDeclaration?): GoFunctionOrMethodDeclaration? {
        if (goFunctionDeclaration == null) return null
        val functionName = goFunctionDeclaration.name ?: return null
        val containingFile = goFunctionDeclaration.containingFile.virtualFile
        val possibleFile = containingFile.nameWithoutExtension + VacuumNamings.testFileSuffix
        val virtualFile = containingFile.parent.findChild(possibleFile) ?: return null
        val goFile = PsiManager.getInstance(goFunctionDeclaration.project)
            .findFile(virtualFile) as? GoFile ?: return null
        return goFile
            .functions
            .firstOrNull { fn -> fn.name != null && fn.name in VacuumNamings.getPossibleUnitTestNames(functionName)  }
    }


    //FIXME get all children without whitespaces to compare if a block/branch is same
    fun getTextWithoutSpaces(given: PsiElement): String {
        val iterator = given.nextLeafs.iterator()
        var acc = ""
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next == given.lastChild.node) break
            if (next !is PsiWhiteSpace) acc+=next.text
        }
        return acc
    }

    private fun unwrapParenthesisExpr(given: PsiElement): PsiElement {
        if (given is GoParenthesesExpr) {
            return given.unwrapParentheses() ?: given
        }
        return given
    }

    fun wrapWithFunction(project: Project, expressions: List<GoStatement>): GoFunctionDeclaration? {
        var expressionsText = ""
        for (expression in expressions) {
            expressionsText = expressionsText + expression.text + "\n"
        }
        return GoElementFactory
            .createElement(
                project,
                "package a; func _() {\n" +
                        expressionsText +
                        "}",
                GoFunctionDeclaration::class.java
            )
    }

    fun isInverted(conditionalExpr: GoConditionalExpr): Pair<Boolean, PsiElement?> {
        var parent = conditionalExpr.parent
        while (parent != null && parent is GoParenthesesExpr) {
            parent = parent.parent
        }

        if (parent == null) {
            return Pair(false, null)
        }

        if (parent is GoUnaryExpr && parent.not != null && parent.not?.text == "!") {
            return Pair(true, parent)
        }

        return Pair(false, null)
    }

    fun isCondition(element: PsiElement): Boolean {
        var parent: PsiElement? = element.parent

        while (parent != null) {
            if (parent is GoIfStatement) return parent.condition?.unwrapParentheses() == element
            parent = parent.parent
        }

        return false
    }

    fun createEmptyElseExpression(project: Project): GoElseStatement? {
        return GoElementFactory
            .createElement(
                project,
                "package a; func _() { if a { } else {} }",
                GoIfStatement::class.java
            )?.elseStatement
    }

    fun getCaret(project: Project): CaretModel? {
        FileEditorManager.getInstance(project).selectedEditor?.let {
            if (it is TextEditor) {
                return it.editor.caretModel
            }
        }
        return null
    }

    fun hasDefault(switchStatement: GoExprSwitchStatement): Boolean {
        if (switchStatement.exprCaseClauseList.isEmpty()) {
            return false
        }
        return switchStatement.exprCaseClauseList.last().default?.text == "default"
    }

    fun isInner(fd: GoFunctionOrMethodDeclaration): Boolean {
        var parent: PsiElement? = fd.parent

        while (parent != null) {
            if (parent is GoFunctionOrMethodDeclaration) {
                return true
            }
            parent = parent.parent
        }

        return false
    }

    fun isNested(switchStatement: GoSwitchStatement): Boolean {
        var parent: PsiElement? = switchStatement.parent

        while (parent != null) {
            if (parent is GoFunctionDeclaration) {
                return false
            }
            if (parent is GoSwitchStatement) {
                return true
            }
            parent = parent.parent
        }

        return false
    }

    fun getCommentLineStart(goBlock: GoBlock?): PsiElement? {
        if (goBlock == null) return null
        return goBlock.children.find { e -> e.elementType == GoParserDefinition.Lazy.LINE_COMMENT }
    }

    fun getDocument(element: PsiElement): Document? {
        return PsiDocumentManager.getInstance(element.project).getDocument(element.containingFile)
    }

    fun isEmpty(goFunctionDeclaration: GoFunctionOrMethodDeclaration): Boolean {
        return goFunctionDeclaration.block?.children?.none { e -> e !is PsiWhiteSpace &&
                e.text != "{" &&
                e.text != "}" } ?: return false
    }

    fun isEmpty(goBlock: GoBlock): Boolean {
        return goBlock.children.none { e -> e !is PsiWhiteSpace &&
                e.text != "{" &&
                e.text != "}" }
    }

    fun getNestingLevelWithFunction(psiElement: PsiElement): Pair<Int, GoFunctionOrMethodDeclaration?> {
        var foundFunction: GoFunctionOrMethodDeclaration? = null
        var nestingLevel = -1
        var parent = psiElement.parent
        while (parent != null) {
            when (parent) {
                is GoFunctionOrMethodDeclaration -> {
                    foundFunction = parent
                    nestingLevel++
                }
                is GoIfStatement -> {
                    nestingLevel++
                }
                is GoSwitchStatement -> {
                    nestingLevel++
                }
                is GoForStatement -> {
                    nestingLevel++
                }
            }
            parent = parent.parent
        }

        return if (nestingLevel == -1) {
            Pair(0, foundFunction)
        } else {
            Pair(nestingLevel, foundFunction)
        }
    }

    fun extractUnary(assignmentStatement: GoAssignmentStatement?): GoUnaryExpr? {
        if (assignmentStatement == null || assignmentStatement.expressionList.isEmpty()) {
            return null
        }
        return assignmentStatement.expressionList[0] as? GoUnaryExpr
    }

    fun isIndented(expression: PsiElement?): Boolean {
        expression ?: return false
        return expression.prevSibling is PsiWhiteSpace
    }

    fun containsImport(goFile: GoFile, importPath: String): Boolean {
        return goFile.imports.any { imp ->
            imp.path == importPath
        }
    }
}
