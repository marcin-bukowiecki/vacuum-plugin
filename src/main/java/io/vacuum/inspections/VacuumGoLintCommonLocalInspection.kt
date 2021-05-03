/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.PsiElement
import io.vacuum.lint.GoLintProcess
import io.vacuum.lint.GoLintResult

/**
 * Inspection for golint process
 *
 * @author Marcin Bukowiecki
 */
class VacuumGoLintCommonLocalInspection : VacuumBaseLocalInspection() {

    private var lintResult: GoLintResult? = null

    override fun inspectionStarted(session: LocalInspectionToolSession, isOnTheFly: Boolean) {
        lintResult = GoLintProcess(session.file).execute()
        super.inspectionStarted(session, isOnTheFly)
    }

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitElement(element: PsiElement) {
                lintResult?.tryRegisterProblem(element, holder)
            }
        }
    }
}
