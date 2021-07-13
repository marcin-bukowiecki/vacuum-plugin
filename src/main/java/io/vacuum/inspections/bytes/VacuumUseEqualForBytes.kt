/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.bytes

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoCallExpr
import com.goide.psi.GoReferenceExpression
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoTypeUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.bytes.BytesCompareQuickFix
import io.vacuum.quickfix.bytes.UseEqualForBytesQuickfix
import io.vacuum.quickfix.bytes.BytesCompareQuickFixProvider
import io.vacuum.quickfix.bytes.UseNotEqualForBytesQuickfix
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumUseEqualForBytes : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitCallExpr(callExpr: GoCallExpr) {
                val binaryExpr = callExpr.parent as? GoBinaryExpr ?: return
                val operator = binaryExpr.operator ?: return

                val right = binaryExpr.right ?: return
                if (!right.isConstant) return

                val rightText = right.text
                if (rightText != "0") return

                if (callExpr.firstChild is GoReferenceExpression) {
                    val firstChild = callExpr.firstChild.firstChild
                    val lastChild = callExpr.firstChild.lastChild

                    if (lastChild is LeafPsiElement && firstChild.text == "bytes" && lastChild.text == "Compare") {
                        val expressionList = callExpr.argumentList.expressionList

                        if (expressionList.size == 2) {
                            val typesOfExpressions = GoTypeUtil.getTypesOfExpressions(expressionList)

                            if (typesOfExpressions[0].presentationText == "[]byte" &&
                                typesOfExpressions[1].presentationText == "[]byte") {

                                getQuickFixProvider(operator.text, rightText)?.let { provider ->
                                    provider.providerQuickFix(callExpr, binaryExpr)?.let { quickFix ->
                                        holder.registerProblem(
                                            callExpr,
                                            VacuumBundle.vacuumInspectionMessage("vacuum.bytes.compare"),
                                            quickFix
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getQuickFixProvider(operator: String, right: String): BytesCompareQuickFixProvider? {
        return if (operator == "==" && right == "0") {
            object : BytesCompareQuickFixProvider() {

                override fun providerQuickFix(callExpr: GoCallExpr, binaryExpr: GoBinaryExpr): BytesCompareQuickFix {
                    return UseEqualForBytesQuickfix(
                        SmartPointerManager.createPointer(callExpr),
                        SmartPointerManager.createPointer(binaryExpr)
                    )
                }
            }
        } else if (operator == "!=" && right == "0") {
            object : BytesCompareQuickFixProvider() {

                override fun providerQuickFix(callExpr: GoCallExpr, binaryExpr: GoBinaryExpr): BytesCompareQuickFix {
                    return UseNotEqualForBytesQuickfix(
                        SmartPointerManager.createPointer(callExpr),
                        SmartPointerManager.createPointer(binaryExpr)
                    )
                }
            }
        } else {
            null
        }
    }
}