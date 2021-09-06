/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.intentions.aws

import com.goide.psi.GoFile
import com.goide.psi.GoStringLiteral
import com.goide.psi.impl.GoElementFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import io.vacuum.intentions.VacuumIntentionBaseAction
import io.vacuum.utils.VacuumBundle

/**
 * Intention to create go unit test
 */
class VacuumToAWSStringIntention : VacuumIntentionBaseAction(VacuumBundle.message("vacuum.aws.string.create")) {

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        if (file !is GoFile) return false

        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset) ?: return false

        if (element.parent !is GoStringLiteral) return false

        return true
    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        val str = element?.parent as? GoStringLiteral ?: return
        val createExpression = GoElementFactory.createCallExpression(project, "aws.String(${str.text})")

        ApplicationManager.getApplication().runWriteAction {
            str.replace(createExpression)
            PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        }
    }
}
