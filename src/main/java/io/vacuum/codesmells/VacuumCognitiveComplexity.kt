/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoForStatement
import com.goide.psi.GoFunctionDeclaration
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.goide.psi.GoIfStatement
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoSwitchStatement
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.PsiElement
import io.vacuum.configurable.Configurable
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class VacuumCognitiveComplexity : VacuumBaseLocalInspection(), Configurable {

    override fun buildGoVisitor(
        holder: GoProblemsHolder,
        session: LocalInspectionToolSession
    ): GoVisitor {

        return object : GoVisitor() {

            private var cognitiveComplexityRegister = mutableMapOf<GoFunctionOrMethodDeclaration,
                    CognitiveComplexityContext>()

            override fun visitFunctionDeclaration(goFunctionDeclaration: GoFunctionDeclaration) {
                checkForContext(goFunctionDeclaration)?.let {
                    val cognitiveComplexityLimit = getCognitiveComplexityLimit()
                    if (cognitiveComplexityLimit.isValid() &&
                        it.getCognitiveComplexityCounter() > cognitiveComplexityLimit
                    ) {
                        holder.registerProblem(goFunctionDeclaration,
                            VacuumInspectionMessage("Function is too complex. Cognitive complexity" +
                                " is ${it.getCognitiveComplexityCounter()} and exceeds the limit" +
                                " of $cognitiveComplexityLimit"))
                    }
                }
            }

            override fun visitMethodDeclaration(goMethodDeclaration: GoMethodDeclaration) {
                checkForContext(goMethodDeclaration)?.let {
                    val cognitiveComplexityLimit = getCognitiveComplexityLimit()
                    if (cognitiveComplexityLimit.isValid() &&
                        it.getCognitiveComplexityCounter() > cognitiveComplexityLimit
                    ) {
                        holder.registerProblem(goMethodDeclaration,
                            VacuumInspectionMessage("Method is too complex. Cognitive complexity" +
                                    " is ${it.getCognitiveComplexityCounter()} and exceeds the limit" +
                                    " of $cognitiveComplexityLimit"))
                    }
                }
            }

            override fun visitIfStatement(ifStatement: GoIfStatement) {
                val result = VacuumPsiUtils.getNestingLevelWithFunction(ifStatement)
                checkForContext(result.second)?.let {
                    it.incrCognitiveComplexityCounter(1 + result.first)
                    checkConditionDepth(ifStatement, result.first)
                }
            }

            override fun visitSwitchStatement(switchStatement: GoSwitchStatement) {
                val result = VacuumPsiUtils.getNestingLevelWithFunction(switchStatement)
                checkForContext(result.second)?.let {
                    it.incrCognitiveComplexityCounter(1 + result.first)
                    checkConditionDepth(switchStatement, result.first)
                }
            }

            override fun visitForStatement(forStatement: GoForStatement) {
                val result = VacuumPsiUtils.getNestingLevelWithFunction(forStatement)
                checkForContext(result.second)?.let {
                    it.incrCognitiveComplexityCounter(1 + result.first)
                    checkConditionDepth(forStatement, result.first)
                }
            }

            private fun checkForContext(function: GoFunctionOrMethodDeclaration?): CognitiveComplexityContext? {
                function?.let { fd ->
                    return if (fd !in this.cognitiveComplexityRegister) {
                        val cognitiveComplexityContext = CognitiveComplexityContext()
                        this.cognitiveComplexityRegister[fd] = cognitiveComplexityContext
                        cognitiveComplexityContext
                    } else {
                        this.cognitiveComplexityRegister[fd]
                    }
                }
                return null
            }

            private fun checkConditionDepth(element: PsiElement, depth: Int) {
                val maxControlFlowDepth = getMaxControlFlowDepth()
                if (maxControlFlowDepth.isValid() && depth > maxControlFlowDepth) {
                    holder.registerProblem(element, VacuumInspectionMessage("Control flow too deep"))
                }
            }
        }
    }
}
