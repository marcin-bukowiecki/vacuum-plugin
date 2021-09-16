/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.bugs

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoShortVarDeclaration
import com.goide.psi.GoType
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoTypeUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.SmartPointerManager
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.error.UnhandledErrorQuickfix
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumUnhandledErrorIntention : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitShortVarDeclaration(declaration: GoShortVarDeclaration) {
                val types = GoTypeUtil.getTypesOfExpressions(declaration.expressionList)
                var errType: GoType? = null
                var index = 0
                for (goExpression in declaration.expressionList) {
                    for (type in types) {
                        if (GoTypeUtil.isError(type, goExpression)) {
                            errType = type
                            break
                        }
                        index++
                    }
                }
                errType?.let {
                    if (index < declaration.varDefinitionList.size) {
                        val errExpr = declaration.varDefinitionList[index]
                        if (errExpr.text == "_") {
                            holder.registerProblem(
                                errExpr,
                                VacuumBundle.vacuumInspectionMessage("vacuum.err.unhandled"),
                                UnhandledErrorQuickfix(
                                    SmartPointerManager.createPointer(declaration),
                                    SmartPointerManager.createPointer(errExpr)
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
