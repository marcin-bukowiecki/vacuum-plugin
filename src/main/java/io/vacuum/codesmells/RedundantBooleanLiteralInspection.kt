/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoReferenceExpression
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage
import io.vacuum.quickfix.RedundantBooleanLiteralQuickFix
import io.vacuum.quickfix.helpers.LeftRedundantBooleanLiteralStrategy
import io.vacuum.quickfix.helpers.RedundantBooleanLiteralStrategy
import io.vacuum.quickfix.helpers.RightRedundantBooleanLiteralStrategy
import io.vacuum.utils.VacuumPsiUtils
import io.vacuum.utils.VacuumPsiUtils.toSmartPointer
import io.vacuum.utils.VacuumUtils.isEq
import io.vacuum.utils.VacuumUtils.isFalse
import io.vacuum.utils.VacuumUtils.isNotEq
import io.vacuum.utils.VacuumUtils.isTrue

/**
 * Checks if given true, false literal is redundant i.e. expr == true, expr != false
 *
 * @author Marcin Bukowiecki
 */
class RedundantBooleanLiteralInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {

        return object : GoVisitor() {

            override fun visitReferenceExpression(ref: GoReferenceExpression) {
                if (ref.isConstant) {
                    if (ref.text.isTrue()) {
                        checkRedundantTrueLiteral(ref, holder)
                    } else if (ref.text.isFalse()) {
                        checkRedundantFalseLiteral(ref, holder)
                    }
                }
            }
        }
    }

    private fun checkRedundantTrueLiteral(referenceExpression: GoReferenceExpression,
                                          holder: GoProblemsHolder) {

        VacuumPsiUtils.findParent<GoBinaryExpr>(referenceExpression, GoBinaryExpr::class.java)?.let { binary ->

            if (binary.operator?.text?.isEq() == true) {
                holder.registerProblem(
                    referenceExpression,
                    VacuumInspectionMessage("Redundant true literal"),
                    RedundantBooleanLiteralQuickFix(createStrategy(binary, referenceExpression),
                        referenceExpression.toSmartPointer(),
                        binary.toSmartPointer()))
            }
        }
    }

    private fun checkRedundantFalseLiteral(referenceExpression: GoReferenceExpression,
                                          holder: GoProblemsHolder) {

        VacuumPsiUtils.findParent<GoBinaryExpr>(referenceExpression, GoBinaryExpr::class.java)?.let { binary ->

            if (binary.operator?.text?.isNotEq() == true) {
                holder.registerProblem(
                    referenceExpression,
                    VacuumInspectionMessage("Redundant false literal"),
                    RedundantBooleanLiteralQuickFix(createStrategy(binary, referenceExpression),
                        referenceExpression.toSmartPointer(),
                        binary.toSmartPointer()))
            }
        }
    }

    private fun createStrategy(binaryExpr: GoBinaryExpr,
                               referenceExpression: GoReferenceExpression): RedundantBooleanLiteralStrategy {
        return if (VacuumPsiUtils.isRight(binaryExpr, referenceExpression)) {
            RightRedundantBooleanLiteralStrategy()
        } else {
            LeftRedundantBooleanLiteralStrategy()
        }
    }
}
