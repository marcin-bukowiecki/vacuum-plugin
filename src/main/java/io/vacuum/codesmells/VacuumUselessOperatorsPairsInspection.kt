/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoAssignmentStatement
import com.goide.psi.GoExpression
import com.goide.psi.GoUnaryExpr
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemHighlightType
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.UselessOperatorQuickFix
import io.vacuum.quickfix.operators.IfNotEqualQuickFix
import io.vacuum.quickfix.operators.NegExprQuickFix
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumPsiUtils
import io.vacuum.utils.VacuumPsiUtils.toSmartPointer

/**
 * @author Marcin Bukowiecki
 */
class VacuumUselessOperatorsPairsInspection : VacuumBaseLocalInspection() {

    private val supportedOperators = setOf("!", "-", "+")

    private val operatorReplacement = mapOf(
        "-" to "-=",
        "+" to "+=",
    )

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {

        return object : GoVisitor() {

            override fun visitAssignmentStatement(goAssignmentStatement: GoAssignmentStatement) {
                super.visitAssignmentStatement(goAssignmentStatement)

                if (goAssignmentStatement.leftHandExprList.expressionList.size == 1 &&
                    goAssignmentStatement.expressionList.size == 1
                ) {

                    VacuumPsiUtils.extractUnary(goAssignmentStatement)?.let { unaryExpr ->
                        if (unaryExpr.operator != null && unaryExpr.operator!!.text in supportedOperators) {

                            if (VacuumPsiUtils.isIndented(goAssignmentStatement.children.last())) {
                                return
                            }

                            holder.registerProblem(
                                goAssignmentStatement,
                                VacuumBundle.vacuumInspectionMessage("vacuum.result.ignored"),
                                ProblemHighlightType.WEAK_WARNING,
                                *getReplacementQuickFix(goAssignmentStatement.leftHandExprList.expressionList[0],
                                    goAssignmentStatement,
                                    unaryExpr.operator!!.text,
                                    unaryExpr)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getReplacementQuickFix(leftHandExpr: GoExpression,
                                       goAssignmentStatement: GoAssignmentStatement,
                                       opText: String,
                                       rightHandExpr: GoUnaryExpr): Array<LocalQuickFix> {

        if (opText == "!") {
            return arrayOf(
                NegExprQuickFix(
                    leftHandExpr.toSmartPointer(),
                    rightHandExpr.toSmartPointer(),
                    goAssignmentStatement.toSmartPointer(),
                ),
                IfNotEqualQuickFix(
                    leftHandExpr.toSmartPointer(),
                    rightHandExpr.toSmartPointer(),
                    goAssignmentStatement.toSmartPointer(),
                )
            )
        }

        val replacementText = operatorReplacement[opText] ?: return emptyArray()
        return arrayOf(UselessOperatorQuickFix(leftHandExpr.toSmartPointer(),
            goAssignmentStatement.toSmartPointer(),
            replacementText,
            rightHandExpr.toSmartPointer()))
    }
}
