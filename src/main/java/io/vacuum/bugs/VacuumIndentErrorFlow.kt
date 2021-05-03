/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.bugs

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoConditionalExpr
import com.goide.psi.GoIfStatement
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoExpressionUtil
import com.goide.psi.impl.GoTypeUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumIndentErrorFlow : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitIfStatement(ifStatement: GoIfStatement) {
                super.visitIfStatement(ifStatement)
                if (ifStatement.elseStatement == null) return

                if (ifStatement.condition != null && ifStatement.condition is GoConditionalExpr) {
                    val condExpr = ifStatement.condition as GoConditionalExpr

                    if (condExpr.expressionList.size == 2) {
                        val types = GoTypeUtil.getTypesOfExpressions(condExpr.expressionList)

                        if (condExpr.notEq != null) {

                            if (GoTypeUtil.isError(types.first(), null) &&
                                GoExpressionUtil.isNil(condExpr.expressionList.last())) {

                                registerProblem(holder, ifStatement)
                            } else if (GoTypeUtil.isError(types.last(), null) &&
                                GoExpressionUtil.isNil(condExpr.expressionList.first())) {

                                registerProblem(holder, ifStatement)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun registerProblem(holder: GoProblemsHolder, ifStatement: GoIfStatement) {
        ifStatement.`if`.let {
            holder.registerProblem(it, VacuumInspectionMessage(VacuumBundle.message("flow.indentErrorFlow")))
        }
    }
}
