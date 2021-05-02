/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix

import com.goide.psi.GoBlock
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.refactoring.suggested.endOffset
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class EmptyCodeBlockQuickFix(private val ptr: SmartPsiElementPointer<GoBlock>) : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Add comment to empty body"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val block = ptr.element ?: return

        val newBlock = block.replace(GoElementFactory.createBlock(project, "\n\t// \n")) as GoBlock

        FileEditorManager.getInstance(project).selectedEditor?.let {
            if (it is TextEditor) {
                val editor = it.editor
                VacuumPsiUtils.getCommentLineStart(newBlock)?.let {
                        comment -> editor.caretModel.moveToOffset(comment.endOffset)
                }
            }
        }
    }
}
