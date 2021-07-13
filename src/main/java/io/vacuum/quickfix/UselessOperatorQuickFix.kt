/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix

import com.goide.psi.GoAssignmentStatement
import com.goide.psi.GoExpression
import com.goide.psi.GoStatement
import com.goide.psi.GoUnaryExpr
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class UselessOperatorQuickFix(
    private val leftHandExprPtr: SmartPsiElementPointer<GoExpression>,
    private val assignmentStatement: SmartPsiElementPointer<GoAssignmentStatement>,
    private val replacement:  String,
    private val unaryExprPtr: SmartPsiElementPointer<GoUnaryExpr>
) : LocalQuickFix {

    override fun getFamilyName(): String {
        return VacuumBundle.message("vacuum.replace.with", replacement)
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        createStatement(project)?.let { createdExpr ->
            assignmentStatement.element?.replace(createdExpr)
        }
    }

    private fun createStatement(project: Project): GoStatement? {
        var text = ""
        text+=leftHandExprPtr.element?.text ?: return null
        text+=replacement
        text+=unaryExprPtr.element?.expression?.text ?: return null

        return GoElementFactory.createStatement(project, text)
    }
}
