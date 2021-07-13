/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.conditions

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPsiElementPointer

/**
 * @author Marcin Bukowiecki
 */
class YodaConditionQuickFix(private val leftRef: SmartPsiElementPointer<PsiElement>,
                            private val operator: String,
                            private val rightRef: SmartPsiElementPointer<PsiElement>) : LocalQuickFix {

    override fun getFamilyName(): String {
        val leftText = leftRef.element?.text ?: ""
        val rightText = rightRef.element?.text ?: ""
        return "Replace with $rightText $operator $leftText"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val leftElement = leftRef.element ?: return
        val rightElement = rightRef.element ?: return
        leftElement.replace(rightElement.copy())
        rightElement.replace(leftElement.copy())
    }
}