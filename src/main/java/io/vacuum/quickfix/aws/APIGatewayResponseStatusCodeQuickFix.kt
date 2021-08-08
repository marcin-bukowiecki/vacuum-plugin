/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.aws

import com.goide.psi.GoElement
import com.goide.psi.GoLiteralValue
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.refactoring.suggested.endOffset
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class APIGatewayResponseStatusCodeQuickFix(private val ref: SmartPsiElementPointer<GoLiteralValue>) : LocalQuickFix {

    override fun getFamilyName(): String {
        return VacuumBundle.getMessage("vacuum.aws.APIGateway.statusCode.quickFix")
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        ref.element?.let { literalValue ->
            val valueElement = GoElementFactory.createLiteralValueElement(ref.project, "StatusCode", "") as? GoElement
                ?: return

            val added = literalValue.addBefore(valueElement, literalValue.lastChild)
            val endOffset = added.endOffset
            FileEditorManager.getInstance(project).selectedEditor?.let {
                if (it is TextEditor) {
                    it.editor.caretModel.moveToOffset(endOffset)
                }
            }
        }
    }
}