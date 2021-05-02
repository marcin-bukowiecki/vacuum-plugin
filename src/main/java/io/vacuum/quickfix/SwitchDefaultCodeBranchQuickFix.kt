/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix

import com.goide.psi.GoExprCaseClause
import com.goide.psi.GoExprSwitchStatement
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.refactoring.suggested.endOffset

/**
 * @author Marcin Bukowiecki
 */
class SwitchDefaultCodeBranchQuickFix(private val expr: SmartPsiElementPointer<GoExprSwitchStatement>) : LocalQuickFix {

    private val toInsert = """
	switch target {
	default:

	}    
    """

    override fun getFamilyName(): String {
        return "Add default switch branch"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = expr.element ?: return

        val created = GoElementFactory.createBlock(project, toInsert)
            .children
            .filterIsInstance<GoExprSwitchStatement>()
            .first().exprCaseClauseList.first()

        val added = element.addBefore(created, element.lastChild) as? GoExprCaseClause ?: return

        FileEditorManager.getInstance(project).selectedEditor?.let {
            if (it is TextEditor) {
                it.editor.caretModel.moveToOffset(added.lastChild.endOffset)
            }
        }
    }
}
