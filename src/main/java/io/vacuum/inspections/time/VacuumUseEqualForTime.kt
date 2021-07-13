/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.time

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoTypeUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.SmartPointerManager
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.time.TimeEqualQuickFix
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumUseEqualForTime : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitBinaryExpr(binaryExpr: GoBinaryExpr) {
                if (binaryExpr.operator?.text != "==") return

                val left = binaryExpr.left
                val right = binaryExpr.right ?: return

                val typesOfExpressions = GoTypeUtil.getTypesOfExpressions(listOf(left, right))
                if (typesOfExpressions.size == 2) {
                    val leftType = typesOfExpressions[0]
                    val rightType = typesOfExpressions[1]
                    if (GoTypeUtil.equalTypes(leftType, rightType, binaryExpr.context, true)) {
                        if (leftType.presentationText == "Time") {
                            holder.registerProblem(
                                binaryExpr,
                                VacuumBundle.vacuumInspectionMessage("vacuum.time.equal"),
                                TimeEqualQuickFix(
                                    SmartPointerManager.createPointer(left),
                                    SmartPointerManager.createPointer(binaryExpr),
                                    SmartPointerManager.createPointer(right)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}