/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.operators

import com.goide.psi.GoAssignmentStatement
import com.goide.psi.GoUnaryExpr
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
class IfNotEqualQuickFix(
    private val leftPtr: SmartPsiElementPointer<PsiElement>,
    private val rightPtr: SmartPsiElementPointer<GoUnaryExpr>,
    private val assignmentStatement: SmartPsiElementPointer<GoAssignmentStatement>
) : VacuumBaseLocalQuickFix(VacuumBundle.message(
    "vacuum.quickfix.replace.itNotEqual",
    leftPtr.element?.text ?: "",
    rightPtr.element?.text ?: "",
)) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val condition = leftPtr.element?.text + "!=" + rightPtr.element?.expression?.text
        val ifStmt = GoElementFactory.createIfStatement(project, condition, "", null)
        ifStmt.let {
            assignmentStatement.element?.replace(it)
        }
    }
}
