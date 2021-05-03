/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoElseStatement
import com.goide.psi.GoIfStatement
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.refactoring.suggested.endOffset
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class VacuumMissingElseBlockInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitElseStatement(o: GoElseStatement) {
                if (o.ifStatement != null && o.ifStatement?.elseStatement == null) {
                    val ifStatement = o.ifStatement as GoIfStatement
                    holder.registerProblem(
                        o.`else`,
                        VacuumInspectionMessage("Expected else statement after else if"),
                        object : LocalQuickFix {

                            override fun getFamilyName(): String {
                                return "Add else statement"
                            }

                            override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
                                val addedElse =
                                    ifStatement.add(VacuumPsiUtils.createEmptyElseExpression(project) ?: return)
                                            as? GoElseStatement

                                VacuumPsiUtils.getCaret(project)?.let { caret ->
                                    addedElse?.let {
                                        val children = it.block?.children ?: return
                                        if (children.isEmpty()) return
                                        caret.moveToOffset(it.block?.children?.get(0)?.endOffset ?: return)
                                    }
                                }
                            }
                        })
                }
            }
        }
    }
}
