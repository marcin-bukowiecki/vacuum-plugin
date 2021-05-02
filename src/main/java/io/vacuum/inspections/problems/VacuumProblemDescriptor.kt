/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.problems

import com.intellij.codeInspection.ProblemDescriptorBase
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement

/**
 * @author Marcin Bukowiecki
 */
@Deprecated("Because passing descriptor is not supported in GoProblemsHolder")
class VacuumProblemDescriptor(
    psiElement: PsiElement,
    description: String,
    highlightType: ProblemHighlightType,
) : ProblemDescriptorBase(
    psiElement, psiElement, description,
    emptyArray(), highlightType, false, null, highlightType != ProblemHighlightType.INFORMATION, true
)
