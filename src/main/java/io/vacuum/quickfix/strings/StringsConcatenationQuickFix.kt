/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.strings

import com.goide.psi.GoAddExpr
import com.goide.psi.GoBinaryExpr
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumPsiUtils
import io.vacuum.utils.VacuumUtils

/**
 * @author Marcin Bukowiecki
 */
class StringsConcatenationQuickFix(private val dataProvider: StringConcatenationQuickFixDataProvider) : LocalQuickFix {

    private val dummyExpr = "1 + 1"

    override fun getFamilyName(): String {
        return VacuumBundle.getMessage("vacuum.strings.concatenation.quickFix.name")
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val elementToReplace = dataProvider.elementToReplace.element ?: return
        val iterator = dataProvider.strings.iterator()
        val separatorElement = dataProvider.separator.element ?: return

        var first = GoElementFactory.createExpression(project, dummyExpr) as GoBinaryExpr
        var current = first as PsiElement
        while (iterator.hasNext()) {
            val next = iterator.next().element ?: return

            if (iterator.hasNext()) {
                val addExpr = GoElementFactory.createExpression(project, dummyExpr) as GoAddExpr
                VacuumPsiUtils.updateBinaryExpr(addExpr, next, separatorElement)
                current.firstChild.replace(addExpr)
                val newCurrent = GoElementFactory.createExpression(project, dummyExpr) as GoAddExpr
                current = current.lastChild.replace(newCurrent)
            } else {
                current.replace(next)
            }
        }

        dataProvider.start?.element?.let { start ->
            first = VacuumPsiUtils.updateBinaryExpr(GoElementFactory.createExpression(project, dummyExpr) as GoAddExpr, start, current)
        }

        val added = elementToReplace.replace(first)
        VacuumUtils.moveCaretToEnd(project, added)
    }
}
