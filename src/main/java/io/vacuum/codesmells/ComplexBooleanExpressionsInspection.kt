/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoIfStatement
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.vacuum.configurable.Configurable
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage

/**
 * @author Marcin Bukowiecki
 */
class ComplexBooleanExpressionsInspection : VacuumBaseLocalInspection(), Configurable {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            var counter = 0

            override fun visitIfStatement(ifStatement: GoIfStatement) {
                val maxNumberOfBooleanExpressions = getMaxNumberOfBooleanExpressions()
                if (maxNumberOfBooleanExpressions.isValid() && counter > maxNumberOfBooleanExpressions) {
                    ifStatement.condition?.let {
                        holder.registerProblem(it,
                            VacuumInspectionMessage("To complex expression. To many && and || operators."))
                    }
                }
                counter = 0
            }

            override fun visitElement(element: PsiElement) {
                if (element is LeafPsiElement && element.text == "if") {
                    counter = 0
                }
            }

            override fun visitBinaryExpr(binaryExpr: GoBinaryExpr) {
                if (binaryExpr.operator == null) return
                if (binaryExpr.operator!!.text !in supportedOperators()) return

                val maxNumberOfBooleanExpressions = getMaxNumberOfBooleanExpressions()
                if (maxNumberOfBooleanExpressions == -1) {
                    return
                }

                counter++
                if (binaryExpr.right != null) {
                    counter++
                }
            }
        }
    }

    private fun supportedOperators(): Set<String> {
        return setOf("&&", "||")
    }
}
