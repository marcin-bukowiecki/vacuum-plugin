/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoFile
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import io.vacuum.lint.GoLintProcess
import io.vacuum.lint.GoLintResult
import io.vacuum.settings.VacuumSettingsComponent
import io.vacuum.settings.VacuumSettingsState

/**
 * Inspection for golint process
 *
 * @author Marcin Bukowiecki
 */
class VacuumGoLintCommonLocalInspection : VacuumBaseLocalInspection() {

    private var lintResult: GoLintResult? = null

    override fun isEnabledOnFile(file: GoFile): Boolean {
        val settings = VacuumSettingsState.getInstance()
        if (!settings.enableGoLint) {
            return false
        }

        val project = file.project
        lintResult = GoLintProcess(file).execute()
        val doc = PsiDocumentManager.getInstance(project).getDocument(file) ?: return false
        if (FileDocumentManager.getInstance().isDocumentUnsaved(doc)) {
            ApplicationManager.getApplication().invokeLater {
                FileDocumentManager.getInstance().saveDocument(doc)
            }
            return false
        }
        return super.isEnabledOnFile(file)
    }

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitElement(element: PsiElement) {
                lintResult?.tryRegisterProblem(element, holder)
            }
        }
    }
}
