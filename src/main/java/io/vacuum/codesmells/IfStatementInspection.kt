/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.psi.GoBlock
import com.goide.psi.GoExpression
import com.goide.psi.GoIfStatement
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoPsiImplUtil
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.DummyHolder

/**
 * Inspection check if given if and else..if statements have same condition and
 * same branch implementation (also with else)
 *
 * @author Marcin Bukowiecki
 */
class IfStatementInspection : LocalInspectionTool() {

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {

        return object : GoVisitor() {

            override fun visitIfStatement(ifStatement: GoIfStatement) {
                if (ifStatement.containingFile is DummyHolder) return

                val collectedBlocks = collectBlocks(ifStatement)
                val textRegister = mutableSetOf<String>()
                for (entry in collectedBlocks) {
                    val text = entry.block.text
                    if (text in textRegister) {
                        holder.registerProblem(entry.psiElement, "Same if/else..if/else block")
                    } else {
                        textRegister.add(text)
                    }
                }
                textRegister.clear()

                val collectedConditions = collectConditions(ifStatement)
                for (condition in collectedConditions) {
                    val text = condition.text
                    if (text in textRegister) {
                        holder.registerProblem(condition, "Same condition with related if/else..if")
                    } else {
                        textRegister.add(text)
                    }
                }
            }
        }
    }

    /**
     * Collect all block from if/else..if/else statements
     */
    private fun collectBlocks(ifStatement: GoIfStatement): List<KeyBlockEntry> {
        val result = mutableListOf<KeyBlockEntry>()
        ifStatement.block?.let { result.add(KeyBlockEntry(ifStatement.`if`, it)) }
        var elseStatement = ifStatement.elseStatement
        while (elseStatement != null) {
            if (elseStatement.ifStatement != null) {
                val elifStatement = elseStatement.ifStatement!!
                elifStatement.block?.let {
                    result.add(KeyBlockEntry(elifStatement.`if`, it))
                }
                elseStatement = elifStatement.elseStatement
            } else if (elseStatement.block != null) {
                elseStatement.block?.let { result.add(KeyBlockEntry(elseStatement.`else`, it)) }
                break
            } else {
                break
            }
        }
        return result
    }

    /**
     * Collect all conditions from if/else..f statements
     */
    private fun collectConditions(ifStatement: GoIfStatement): List<GoExpression> {
        val result = mutableListOf<GoExpression>()
        GoPsiImplUtil.getCondition(ifStatement)?.let { result.add(it) }
        var elseIfStatement = ifStatement.elseStatement?.ifStatement
        while (elseIfStatement != null) {
            GoPsiImplUtil.getCondition(elseIfStatement)?.let { result.add(it) }
            elseIfStatement = elseIfStatement.elseStatement?.ifStatement
        }
        return result
    }

    private data class KeyBlockEntry(val psiElement: PsiElement, val block: GoBlock)
}
