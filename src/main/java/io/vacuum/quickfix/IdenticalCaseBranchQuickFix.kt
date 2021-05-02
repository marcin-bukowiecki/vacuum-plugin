/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix

import com.goide.formatter.GoFormatterUtil
import com.goide.psi.GoExprCaseClause
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.SmartPsiElementPointer

/**
 * @author Marcin Bukowiecki
 */
class IdenticalCaseBranchQuickFix(
    private val toMergeCasePtr: SmartPsiElementPointer<GoExprCaseClause>,
    private val withPtr: SmartPsiElementPointer<GoExprCaseClause>
) : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Merge identical case branches"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        toMergeCasePtr.element?.expressionList?.toList()?.let { toAdd ->
            for (goExpression in toAdd) {
                val colon = withPtr.element?.colon
                val created = GoElementFactory
                    .createElement(project, "package a; func _() { switch a {case " + goExpression.text + ":}}",
                        GoExprCaseClause::class.java) ?: return
                withPtr.element?.addAfter(created, colon)
            }
        }
        toMergeCasePtr.element?.delete()

        if (withPtr.element?.nextSibling is PsiWhiteSpace && withPtr.element?.nextSibling?.text == "\n") {
            withPtr.element?.nextSibling?.delete()
        }

        GoFormatterUtil.reformat(withPtr.element?.containingFile)
    }
}
