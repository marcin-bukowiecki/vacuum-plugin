/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.conditions

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoExpressionUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.SmartPointerManager
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.conditions.YodaConditionQuickFix
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumUtils.isEqOrNotEq

/**
 * @author Marcin Bukowiecki
 */
class VacuumYodaConditionInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitBinaryExpr(binaryExpr: GoBinaryExpr) {
                val operator = binaryExpr.operator ?: return
                val right = binaryExpr.right ?: return
                if (GoExpressionUtil.isNil(right)) return

                if (binaryExpr.left.isConstant && !right.isConstant && operator.text.isEqOrNotEq()) {
                    holder.registerProblem(binaryExpr,
                        VacuumBundle.vacuumInspectionMessage("vacuum.quickfix.yoda"),
                        YodaConditionQuickFix(
                            SmartPointerManager.createPointer(binaryExpr.left),
                            operator.text,
                            SmartPointerManager.createPointer(right)
                        )
                    )
                }
            }
        }
    }
}