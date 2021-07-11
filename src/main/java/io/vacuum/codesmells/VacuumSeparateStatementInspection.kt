/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoForClause
import com.goide.psi.GoIfStatement
import com.goide.psi.GoStatement
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoEmptyStatementImpl
import com.intellij.codeInspection.LocalInspectionToolSession
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumPsiUtils

/**
 * Checks if given Go statements are on same line
 *
 * @author Marcin Bukowiecki
 */
class VacuumSeparateStatementInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder,
                                isOnTheFly: LocalInspectionToolSession): GoVisitor {

        return object : GoVisitor() {

            private val statementRegister = StatementRegister()

            override fun visitStatement(statement: GoStatement) {
                if (canBeIgnored(statement)) {
                    return
                }

                if (statementRegister.addStatement(statement)) {
                    statementRegister.findStatementOnSameLine(statement).forEach { ptr ->
                        ptr.element?.let { found ->
                            if (found !is GoEmptyStatementImpl) {
                                log.debug("Statements on same line")
                                holder.registerProblem(
                                    found,
                                    VacuumBundle.vacuumInspectionMessage("vacuum.statement.sameLine")
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun canBeIgnored(statement: GoStatement): Boolean {
        if (statement is GoIfStatement) {
            return true
        }
        if (VacuumPsiUtils.isCondition(statement)) {
            return true
        }
        if (statement.parent is GoForClause) {
            return true
        }
        return false
    }
}
