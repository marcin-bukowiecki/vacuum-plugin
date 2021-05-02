/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix

import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoReferenceExpression
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.quickfix.helpers.RedundantBooleanLiteralStrategy

/**
 * @author Marcin Bukowiecki
 */
class RedundantBooleanLiteralQuickFix(
    private val strategy: RedundantBooleanLiteralStrategy,
    private val toRemove: SmartPsiElementPointer<GoReferenceExpression>,
    private val binaryExpr: SmartPsiElementPointer<GoBinaryExpr>,
) : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Remove redundant boolean literal"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        strategy.applyFix(project, descriptor, this)
    }

    fun getBinaryExpr(): GoBinaryExpr? {
        return binaryExpr.element
    }

    fun getToRemove(): GoReferenceExpression? {
        return toRemove.element
    }
}
