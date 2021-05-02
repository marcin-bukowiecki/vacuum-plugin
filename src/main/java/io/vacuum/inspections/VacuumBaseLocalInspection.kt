/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections

import com.goide.execution.testing.GoTestFinder
import com.goide.inspections.core.GoInspectionBase
import com.goide.psi.GoBlock
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import io.vacuum.utils.VacuumNamings
import io.vacuum.utils.VacuumPsiUtils
import io.vacuum.utils.VacuumUtils

/**
 * @author Marcin Bukowiecki
 */
abstract class VacuumBaseLocalInspection : GoInspectionBase() {

    private var wholeFile = false

    val log = Logger.getInstance(this::class.java)

    override fun runForWholeFile(): Boolean {
        return wholeFile
    }

    override fun isSuppressedFor(element: PsiElement): Boolean {
        return !isNotTest(element) || super.isSuppressedFor(element)
    }

    open fun caretInBlock(block: GoBlock?): Boolean {
        if (block == null) return false
        val caret = VacuumPsiUtils.getCaret(block.project) ?: return false
        return block.startOffset <= caret.offset && caret.offset <= block.endOffset
    }

    open fun isTest(element: PsiElement): Boolean {
        return GoTestFinder.isTestFile(element.containingFile)
    }

    open fun isNotTest(element: PsiElement): Boolean {
        return !isTest(element)
    }

    @Deprecated("added intention action")
    open fun testNotDefined(fn: GoFunctionOrMethodDeclaration): Boolean {
        val containingFile = fn.containingFile
        val testName = VacuumNamings.getTestFileName(containingFile)
        val foundChild = containingFile.project.projectFile?.findChild(testName) ?: return true
        val foundGoFile = VacuumUtils.asGoFile(fn.project, foundChild) ?: return true
        return GoTestFinder.findTestFunctionInContext(foundGoFile) == null
    }

    fun processFile(
        file: PsiFile,
        manager: InspectionManager,
        wholeFile: Boolean
    ): MutableList<ProblemDescriptor> {
        this.wholeFile = wholeFile
        return super.processFile(file, manager)
    }
}
