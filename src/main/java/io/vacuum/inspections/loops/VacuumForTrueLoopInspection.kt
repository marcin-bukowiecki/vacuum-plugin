/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.loops

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoExpression
import com.goide.psi.GoForStatement
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumUtils.isTrue

/**
 * @author Marcin Bukowiecki
 */
class VacuumForTrueLoopInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitForStatement(o: GoForStatement) {
                val expression = o.expression ?: return
                if (expression.isConstant && expression.text.isTrue()) {
                    holder.registerProblem(
                        expression,
                        VacuumBundle.vacuumInspectionMessage("vacuum.loop.uselessTrueConstant"),
                        VacuumForTrueLoopQuickFix(SmartPointerManager.createPointer(expression))
                    )
                }
            }
        }
    }
}

/**
 * @author Marcin Bukowiecki
 */
class VacuumForTrueLoopQuickFix(private val ptr: SmartPsiElementPointer<GoExpression>) : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Remove true constant"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        ptr.element?.delete()
    }
}
