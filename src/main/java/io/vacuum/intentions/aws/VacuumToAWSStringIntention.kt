/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.intentions.aws

import com.goide.psi.GoExpression
import com.goide.psi.GoFile
import com.goide.psi.GoStringLiteral
import com.goide.psi.impl.GoElementFactory
import com.goide.psi.impl.GoTypeUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.vacuum.intentions.VacuumIntentionBaseAction
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumToAWSStringIntention : VacuumIntentionBaseAction(VacuumBundle.message("vacuum.aws.string.create")) {

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        if (file !is GoFile) return false

        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset - 1) ?: return false
        val parent = element.parent

        if (parent is GoStringLiteral) return true

        if (parent is GoExpression) {
            val typesOfExpressions = GoTypeUtil.getTypesOfExpressions(mutableListOf(parent))
            typesOfExpressions[0]?.let { t ->
                if (GoTypeUtil.isString(t, element)) {
                    return true
                }
            }
        }

        return false
    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset - 1) ?: return
        val parent = element.parent

        getReplaceStrategy(parent, editor)?.replace()
    }

    private fun getReplaceStrategy(parent: PsiElement, editor: Editor): ReplaceStrategy? {
        return when (parent) {
            is GoStringLiteral -> {
                GoStringReplace(parent, editor)
            }
            is GoExpression -> {
                GoExpressionReplace(parent, editor)
            }
            else -> {
                null
            }
        }
    }
}

/**
 * @author Marcin Bukowiecki
 */
interface ReplaceStrategy {

    val editor: Editor

    fun replace()

    fun replace(text: String, toReplace: PsiElement) {
        val project = toReplace.project
        val createExpression = GoElementFactory.createCallExpression(project, "aws.String(${text})")

        ApplicationManager.getApplication().runWriteAction {
            toReplace.replace(createExpression)
            PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        }
    }
}

/**
 * @author Marcin Bukowiecki
 */
class GoStringReplace(private val parent: GoStringLiteral,
                      override val editor: Editor) : ReplaceStrategy {

    override fun replace() {
        val text = parent.text
        replace(text, parent)
    }
}

/**
 * @author Marcin Bukowiecki
 */
class GoExpressionReplace(private val expression: GoExpression,
                          override val editor: Editor): ReplaceStrategy {

    override fun replace() {
        val text = expression.text
        replace(text, expression)
    }
}
