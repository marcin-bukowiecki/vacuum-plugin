/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.time

import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoExpression
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class TimeEqualQuickFix(
    private val leftRef: SmartPsiElementPointer<GoExpression>,
    private val binaryRef: SmartPsiElementPointer<GoBinaryExpr>,
    private val rightRef: SmartPsiElementPointer<GoExpression>
) : LocalQuickFix {

    override fun getFamilyName(): String {
        return VacuumBundle.getMessage("vacuum.time.equal.quickfix")
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val left = leftRef.element ?: return
        val right = rightRef.element ?: return
        val expression = GoElementFactory.createExpression(binaryRef.project, "${left.text}.Equal(${right.text})")
        binaryRef.element?.replace(expression)
    }
}