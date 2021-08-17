/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.concurrency

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoCallExpr
import com.goide.psi.GoExpression
import com.goide.psi.GoSimpleStatement
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoPsiUtil
import com.goide.psi.impl.GoTypeUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.SmartPointerManager
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.concurrency.EmptyCriticalSectionQuickfix
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumEmptyCriticalSectionInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitSimpleStatement(stmt: GoSimpleStatement) {
                if (refIsMutex(stmt) && isLock(stmt)) {
                    var nextSibling = stmt.nextSibling
                    while (GoPsiUtil.isWhiteSpaceOrCommentOrEmpty(nextSibling)) {
                        nextSibling = nextSibling.nextSibling
                    }
                    (nextSibling as? GoSimpleStatement)?.let { nextStmt ->
                        if (refIsMutex(stmt) && isUnlock(nextStmt)) {
                            holder.registerProblem(
                                nextStmt,
                                VacuumBundle.vacuumInspectionMessage("vacuum.concurrency.uselessCriticalSection"),
                                EmptyCriticalSectionQuickfix(SmartPointerManager.createPointer(nextStmt))
                            )
                        }
                    }
                }
            }
        }
    }

    private fun isLock(stmt: GoSimpleStatement): Boolean {
        stmt.leftHandExprList?.let { list ->
            (list.firstChild as? GoCallExpr)?.let { callExpr ->
                return callExpr.expression.lastChild?.text == "Lock"
            }
        }

        return false
    }

    private fun isUnlock(stmt: GoSimpleStatement): Boolean {
        stmt.leftHandExprList?.let { list ->
            (list.firstChild as? GoCallExpr)?.let { callExpr ->
                return callExpr.expression.lastChild?.text == "Unlock"
            }
        }

        return false
    }

    private fun refIsMutex(stmt: GoSimpleStatement): Boolean {
        stmt.leftHandExprList?.let { list ->
            (list.firstChild as? GoCallExpr)?.let { callExpr ->
                (callExpr.expression.firstChild as? GoExpression).let { ref ->
                    val refTypes = GoTypeUtil.getTypesOfExpressions(listOf(ref))
                    if (refTypes.size == 1 && refTypes[0] != null && refTypes[0].text == "Mutex") {
                        return true
                    }
                }
            }
        }

        return false
    }
}