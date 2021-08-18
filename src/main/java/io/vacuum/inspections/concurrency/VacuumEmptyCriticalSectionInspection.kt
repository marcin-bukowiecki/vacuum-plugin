/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.concurrency

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoSimpleStatement
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoPsiUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.SmartPointerManager
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.concurrency.EmptyCriticalSectionQuickfix
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class VacuumEmptyCriticalSectionInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitSimpleStatement(stmt: GoSimpleStatement) {
                if (VacuumPsiUtils.isTypeOf(stmt, "Mutex") && VacuumPsiUtils.isCall(stmt, "Lock")) {
                    var nextSibling = stmt.nextSibling
                    while (GoPsiUtil.isWhiteSpaceOrCommentOrEmpty(nextSibling)) {
                        nextSibling = nextSibling.nextSibling
                    }
                    (nextSibling as? GoSimpleStatement)?.let { nextStmt ->
                        if (VacuumPsiUtils.isTypeOf(stmt, "Mutex") && VacuumPsiUtils.isCall(nextStmt, "Unlock")) {
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
}