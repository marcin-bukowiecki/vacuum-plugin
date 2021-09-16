/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.errors

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoAssignmentStatement
import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoIfStatement
import com.goide.psi.GoReferenceExpression
import com.goide.psi.GoShortVarDeclaration
import com.goide.psi.GoSimpleStatement
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoTypeUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPointerManager
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.error.OverwroteErrorQuickfix
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumOverwroteErrorInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(
        holder: GoProblemsHolder,
        session: LocalInspectionToolSession
    ): GoVisitor {
        return object : GoVisitor() {

            override fun visitAssignmentStatement(o: GoAssignmentStatement) {
                getErrorElement(o)?.let { errorElement ->
                    val text = errorElement.text
                    var prev = o.prevSibling

                    while (prev != null) {
                        if (prev is GoIfStatement && matchesErrorElement(text, prev)) {
                            break
                        } else if (prev is GoAssignmentStatement) {
                            getErrorElement(prev)?.let { prevErrorElement ->
                                if (prevErrorElement.text == text) {
                                    addProblem(holder, prevErrorElement, prevErrorElement.text, prev)
                                }
                            }
                        } else if (prev is GoSimpleStatement && prev.firstChild is GoShortVarDeclaration) {
                            val shortVarDeclaration = prev.firstChild as GoShortVarDeclaration
                            shortVarDeclaration.varDefinitionList.last()?.let { varDef ->
                                val prevType = varDef.getGoType(null)
                                if (GoTypeUtil.isError(prevType, varDef)) {
                                    if (varDef.text == text) {
                                        addProblem(holder, varDef, varDef.text, prev)
                                    }
                                }
                            }
                        }
                        prev = prev.prevSibling
                    }
                }
            }
        }
    }

    private fun addProblem(holder: GoProblemsHolder, toMark: PsiElement, errorText: String, addAfter: PsiElement) {
        holder.registerProblem(
            toMark,
            VacuumBundle.vacuumInspectionMessage("vacuum.err.unhandled"),
            OverwroteErrorQuickfix(errorText, SmartPointerManager.createPointer(addAfter))
        )
    }

    private fun matchesErrorElement(errorText: String, o: GoIfStatement): Boolean {
        o.condition?.unwrapParentheses()?.let {
            if (it is GoBinaryExpr && it.operator?.text == "!=") {
                if (it.left.text == errorText) {
                    return true
                } else if (it.right?.text == errorText) {
                    return true
                }
            }
            return false
        }

        return false
    }

    private fun getErrorElement(o: GoAssignmentStatement): PsiElement? {
        val leftHandExprList = o.leftHandExprList
        val lastChild = leftHandExprList.lastChild
        if (lastChild is GoReferenceExpression) {
            val typesOfExpressions = GoTypeUtil.getTypesOfExpressions(listOf(lastChild))
            typesOfExpressions[0]?.let { t ->
                if (GoTypeUtil.isError(t, o)) {
                    return lastChild
                }
            }
        }
        return null
    }
}
