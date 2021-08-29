/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.println

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoBuiltinCallExpr
import com.goide.psi.GoCallExpr
import com.goide.psi.GoStringLiteral
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumPrintlnInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitCallExpr(o: GoCallExpr) {
                if (o.expression.text == "fmt.Print" && o.argumentList.expressionList.size == 1) {
                    val value = o.argumentList.expressionList[0] ?: return
                    if (value is GoStringLiteral) {
                        val text = value.decodedText
                        if (text.endsWith("\n")) {
                            holder.registerProblem(
                                o,
                                VacuumBundle.vacuumInspectionMessage("vacuum.print.newLineReplace"),
                                object : LocalQuickFix {

                                    override fun getFamilyName(): String {
                                        return VacuumBundle.message("vacuum.print.newLineReplace.replace")
                                    }

                                    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
                                        val createdCallExpression = GoElementFactory.createCallExpression(
                                            o.project,
                                            "fmt.Println(\"${text.trim()}\")"
                                        )
                                        o.replace(createdCallExpression)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            override fun visitBuiltinCallExpr(o: GoBuiltinCallExpr) {
                if (o.expression.text == "print" && o.builtinArgumentList.expressionList.size == 1) {
                    val value = o.builtinArgumentList.expressionList[0] ?: return
                    if (value is GoStringLiteral) {
                        val text = value.decodedText
                        if (text.endsWith("\n")) {
                            holder.registerProblem(
                                o,
                                VacuumBundle.vacuumInspectionMessage("vacuum.print.newLineReplace"),
                                object : LocalQuickFix {

                                    override fun getFamilyName(): String {
                                        return VacuumBundle.message("vacuum.print.builtin.newLineReplace.replace")
                                    }

                                    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
                                        val createdCallExpression = GoElementFactory.createCallExpression(
                                            o.project,
                                            "println(\"${text.trim()}\")"
                                        )
                                        o.replace(createdCallExpression)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}