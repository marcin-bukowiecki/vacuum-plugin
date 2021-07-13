/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.mathpow

import com.goide.psi.GoCallExpr
import com.goide.psi.GoLiteral
import com.goide.psi.GoReferenceExpression
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
abstract class MathPowQuickFix : LocalQuickFix {

    override fun getFamilyName(): String {
        return VacuumBundle.message("vacuum.quickfix.math.pow")
    }
}

/**
 * @author Marcin Bukowiecki
 */
class VariableMathPowQuickFix(private val ref: SmartPsiElementPointer<GoReferenceExpression>,
                              private val callExprRef: SmartPsiElementPointer<GoCallExpr>) : MathPowQuickFix() {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        callExprRef.element?.let { callExpr ->
            val element = ref.element ?: return
            val expression = GoElementFactory.createExpression(ref.project, "${element.text} * ${element.text}")
            callExpr.replace(expression)
        }
    }
}

/**
 * @author Marcin Bukowiecki
 */
class ConstantMathPowQuickFix(private val ref: SmartPsiElementPointer<GoLiteral>,
                              private val callExprRef: SmartPsiElementPointer<GoCallExpr>) : MathPowQuickFix() {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        callExprRef.element?.let { callExpr ->
            val element = ref.element ?: return
            val expression = GoElementFactory.createExpression(ref.project, "${element.text} * ${element.text}")
            callExpr.replace(expression)
        }
    }
}

