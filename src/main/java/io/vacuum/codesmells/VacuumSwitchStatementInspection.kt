/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoExprCaseClause
import com.goide.psi.GoExprSwitchStatement
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.configurable.Configurable
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage
import io.vacuum.quickfix.IdenticalCaseBranchQuickFix
import io.vacuum.quickfix.SwitchDefaultCodeBranchQuickFix
import io.vacuum.utils.VacuumPsiUtils

/**
 * Checks for:
 *
 * - nested switch statements
 * - default switch branch
 * - number of case branches
 * - number of lines in case branch
 * - identical case branches
 *
 * @author Marcin Bukowiecki
 */
class VacuumSwitchStatementInspection : VacuumBaseLocalInspection(), Configurable {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitExprSwitchStatement(switchStatement: GoExprSwitchStatement) {
                if (VacuumPsiUtils.isNested(switchStatement)) {
                    holder.registerProblem(switchStatement, VacuumInspectionMessage("Nested `switch` statement"))
                }
                if (!VacuumPsiUtils.hasDefault(switchStatement)) {
                    holder.registerProblem(
                        switchStatement.switchStart,
                        VacuumInspectionMessage("Expected default branch"),
                        SwitchDefaultCodeBranchQuickFix(SmartPointerManager.createPointer(switchStatement))
                    )
                }

                val size = switchStatement.exprCaseClauseList.size
                if (size == 0 || getMaxNumberOfCases() == -1) {
                    return
                }
                if (size > getMaxNumberOfCases()) {
                    holder.registerProblem(
                        switchStatement,
                        VacuumInspectionMessage("Too many case branches. Maximum is: ${getMaxNumberOfCases()}")
                    )
                }

                val maxNumberOfLinesInCase = getMaxSwitchCaseLines()
                val caseTextRegister = mutableMapOf<String, SmartPsiElementPointer<GoExprCaseClause>>()

                for (goExprCaseClause in switchStatement.exprCaseClauseList) {
                    if (goExprCaseClause.statementList.isEmpty()) {
                        continue
                    }
                    if (maxNumberOfLinesInCase >= 0 && maxNumberOfLinesInCase < goExprCaseClause.statementList.size) {
                        holder.registerProblem(
                            switchStatement,
                            VacuumInspectionMessage("Case branch has to many lines. Maximum is: $maxNumberOfLinesInCase")
                        )
                    }

                    VacuumPsiUtils.wrapWithFunction(
                        goExprCaseClause.project,
                        goExprCaseClause.statementList
                    )?.takeIf { fd -> fd.isValid }?.let { fd ->
                        val text = fd.text
                        if (text in caseTextRegister) {
                            holder.registerProblem(goExprCaseClause,
                                VacuumInspectionMessage("Identical case branch"),
                                IdenticalCaseBranchQuickFix(SmartPointerManager.createPointer(goExprCaseClause),
                                caseTextRegister[text]!!)
                            )
                        } else {
                            caseTextRegister.put(text, SmartPointerManager.createPointer(goExprCaseClause))
                        }
                    }
                }
            }
        }
    }
}
