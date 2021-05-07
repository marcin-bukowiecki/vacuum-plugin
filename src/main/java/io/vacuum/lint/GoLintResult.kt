/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.lint

import com.goide.inspections.core.GoProblemsHolder
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import io.vacuum.inspections.problems.VacuumInspectionMessage

/**
 * @author Marcin Bukowiecki
 */
class GoLintResult(private val messages: List<GoLintMessage>): LintResult<GoLintMessage> {

    override fun getLintMessages(): List<GoLintMessage> {
        return messages
    }

    override fun tryRegisterProblem(psiElement: PsiElement, holder: GoProblemsHolder): Boolean {
        var marked = false
        messages
            .filter { !it.isRegistered() }
            .forEach {
            if (it.psiMatches(psiElement)) {
                holder.registerProblem(psiElement, VacuumInspectionMessage(it.message()), ProblemHighlightType.WARNING)
                it.setRegistered(true)
                marked = true
            }
        }
        return marked
    }

    fun isEmpty(): Boolean {
        return messages.isEmpty()
    }
}
