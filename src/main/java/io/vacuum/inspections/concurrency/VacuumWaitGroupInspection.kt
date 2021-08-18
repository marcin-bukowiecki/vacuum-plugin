/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.concurrency

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.goide.psi.GoGoStatement
import com.goide.psi.GoSimpleStatement
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoPsiImplUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class VacuumWaitGroupInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder,
                                session: LocalInspectionToolSession): GoVisitor {

        return object : GoVisitor() {

            override fun visitGoStatement(o: GoGoStatement) {
                o.expression?.let { expr ->
                    val block = if (expr is GoFunctionOrMethodDeclaration) {
                        expr.blockIfExists
                    } else {
                        GoPsiImplUtil.resolveCall(expr)?.blockIfExists
                    }

                    block?.children?.forEach {
                        (it as? GoSimpleStatement)?.let { stmt ->
                            checkFunctionStatement(stmt)
                        }
                    }
                }
            }

            private fun checkFunctionStatement(o: GoSimpleStatement) {
                if (VacuumPsiUtils.isTypeOf(o, "WaitGroup") && VacuumPsiUtils.isCall(o, "Add")) {
                    holder.registerProblem(
                        o,
                        VacuumBundle.vacuumInspectionMessage("vacuum.concurrency.goRoutines.threadRace")
                    )
                }
            }
        }
    }
}