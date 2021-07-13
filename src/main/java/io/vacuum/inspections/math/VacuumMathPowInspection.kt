/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.math

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoCallExpr
import com.goide.psi.GoLiteral
import com.goide.psi.GoReferenceExpression
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPointerManager
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.mathpow.ConstantMathPowQuickFix
import io.vacuum.quickfix.mathpow.MathPowQuickFix
import io.vacuum.quickfix.mathpow.VariableMathPowQuickFix
import io.vacuum.utils.VacuumBundle
import org.apache.commons.lang3.math.NumberUtils

/**
 * @author Marcin Bukowiecki
 */
class VacuumMathPowInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, toolSession: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitCallExpr(callExpr: GoCallExpr) {
                val expression = callExpr.expression
                val text = expression.text
                if (text == "math.Pow") {
                    val expressions = callExpr.argumentList.expressionList
                    val exponent = expressions[1]
                    if (expressions.size == 2 && exponent is GoLiteral && exponent.isConstant) {
                        if (NumberUtils.isCreatable(exponent.text)) {
                            val base = expressions[0]
                            getIntentionHandler(base, callExpr)?.let { quickFix ->
                                holder.registerProblem(
                                    callExpr,
                                    VacuumBundle.vacuumInspectionMessage("vacuum.quickfix.math.pow"),
                                    quickFix
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getIntentionHandler(element: PsiElement, callExpr: GoCallExpr): MathPowQuickFix? {
        if (element is GoReferenceExpression && element.references.size == 1) {
            element.references[0].resolve()?.let {
                return VariableMathPowQuickFix(
                    SmartPointerManager.createPointer(element),
                    SmartPointerManager.createPointer(callExpr)
                )
            }
        } else if (element is GoLiteral && element.isConstant && NumberUtils.isCreatable(element.text)) {
            return ConstantMathPowQuickFix(
                SmartPointerManager.createPointer(element),
                SmartPointerManager.createPointer(callExpr)
            )
        }

        return null
    }
}