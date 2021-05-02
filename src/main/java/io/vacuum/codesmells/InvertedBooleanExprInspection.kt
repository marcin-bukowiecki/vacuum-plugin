/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.psi.GoConditionalExpr
import com.goide.psi.GoExpression
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class InvertedBooleanExprInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GoVisitor() {

            override fun visitConditionalExpr(conditionalExpr: GoConditionalExpr) {
                val result = VacuumPsiUtils.isInverted(conditionalExpr)
                if (!result.first) {
                    return
                }

                holder.registerProblem(conditionalExpr, "Inverted boolean check",
                    object : LocalQuickFix {

                        override fun getFamilyName(): String {
                            return "Invert boolean check"
                        }

                        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
                            negateExpr(conditionalExpr)?.let { expr ->
                                result.second?.replace(expr)
                            }
                        }
                    })
            }
        }
    }

    private fun negateExpr(conditionalExpr: GoConditionalExpr): GoExpression? {
        getInverted(conditionalExpr.operator?.text).let { operator ->
            return if (operator.isEmpty()) {
                null
            } else {
                GoElementFactory.createExpression(conditionalExpr.project,
                    conditionalExpr.left.text + operator + conditionalExpr.right?.text)
            }
        }
    }

    private fun getInverted(text: String?): String {
        return when (text) {
            "==" -> "!="
            "!=" -> "=="
            "<" -> ">="
            ">" -> "<="
            ">=" -> "<"
            "<=" -> ">"
            else -> ""
        }
    }
}
