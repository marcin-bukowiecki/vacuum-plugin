/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.slices

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoCallExpr
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.goide.psi.GoLiteral
import com.goide.psi.GoResolvable
import com.goide.psi.GoUnaryExpr
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.PsiElement
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumUtils.isLesser

/**
 * @author Marcin Bukowiecki
 */
class VacuumUselessSliceLengthCheckInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitBinaryExpr(goBinaryExpr: GoBinaryExpr) {
                val text = goBinaryExpr.operator?.text ?: return
                if (!text.isLesser()) return

                val right = goBinaryExpr.right ?: return
                if ((right is GoLiteral && right.text == "0") || goBinaryExpr.right is GoUnaryExpr) {
                    if (isCall(goBinaryExpr.left, "len") || isCall(goBinaryExpr.left, "cap")) {
                        holder.registerProblem(
                            goBinaryExpr.left,
                            VacuumBundle.vacuumInspectionMessage("vacuum.slice.uselessLengthCheck")
                        )
                    }
                }
            }
        }
    }

    private fun isCall(element: PsiElement, call: String): Boolean {
        if (element !is GoCallExpr) return false

        (element.expression as? GoResolvable)?.resolve()?.let { resolved ->
            (resolved as? GoFunctionOrMethodDeclaration)?.let {
                return it.name == call
            }
        }

        return false
    }
}