/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.bytes

import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoCallExpr
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
abstract class BytesCompareQuickFixProvider {

    abstract fun providerQuickFix(callExpr: GoCallExpr, binaryExpr: GoBinaryExpr): BytesCompareQuickFix?
}

/**
 * @author Marcin Bukowiecki
 */
interface BytesCompareQuickFix : LocalQuickFix

/**
 * @author Marcin Bukowiecki
 */
class UseEqualForBytesQuickfix(private val callExprRef: SmartPsiElementPointer<GoCallExpr>,
                               private val binaryExprRef: SmartPsiElementPointer<GoBinaryExpr>) : BytesCompareQuickFix {

    override fun getFamilyName(): String {
        return VacuumBundle.getMessage("vacuum.bytes.compare.quickfix")
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        callExprRef.element?.let { callExpr ->
            val args = callExpr.argumentList.text
            binaryExprRef.element?.let { binaryExpr ->
                val newExpr = GoElementFactory.createCallExpression(callExpr.project, "bytes.Equal$args")
                binaryExpr.replace(newExpr)
            }
        }
    }
}

/**
 * @author Marcin Bukowiecki
 */
class UseNotEqualForBytesQuickfix(private val callExprRef: SmartPsiElementPointer<GoCallExpr>,
                                  private val binaryExprRef: SmartPsiElementPointer<GoBinaryExpr>) : BytesCompareQuickFix {

    override fun getFamilyName(): String {
        return VacuumBundle.getMessage("vacuum.bytes.compare.quickfix.not")
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        callExprRef.element?.let { callExpr ->
            val args = callExpr.argumentList.text
            binaryExprRef.element?.let { binaryExpr ->
                val newExpr = GoElementFactory.createExpression(callExpr.project, "!bytes.Equal$args")
                binaryExpr.replace(newExpr)
            }
        }
    }
}