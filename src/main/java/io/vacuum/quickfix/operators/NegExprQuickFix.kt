/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.operators

import com.goide.psi.GoAssignmentStatement
import com.goide.psi.GoStatement
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.quickfix.VacuumBaseLocalQuickFix
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class NegExprQuickFix(
    private val leftPtr: SmartPsiElementPointer<PsiElement>,
    private val rightPtr: SmartPsiElementPointer<PsiElement>,
    private val assignmentStatement: SmartPsiElementPointer<GoAssignmentStatement>
) : VacuumBaseLocalQuickFix(VacuumBundle.message(
    "quickfix.replace.negExpr",
    leftPtr.element?.text ?: "",
    rightPtr.element?.text ?: "",
)) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        createStatement(project)?.let { createdExpr ->
            assignmentStatement.element?.replace(createdExpr)
        }
    }

    private fun createStatement(project: Project): GoStatement? {
        var text = ""
        text+=leftPtr.element?.text ?: return null
        text+="="
        text+=" "
        text+=rightPtr.element?.text ?: return null

        return GoElementFactory.createStatement(project, text)
    }
}
