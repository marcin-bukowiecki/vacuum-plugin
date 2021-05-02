/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.lint

import com.goide.inspections.core.GoProblemsHolder
import com.intellij.psi.PsiElement

/**
 * @author Marcin Bukowiecki
 */
interface LintResult<R : LintMessage> {

    fun getLintMessages(): List<R>

    fun tryRegisterProblem(psiElement: PsiElement, holder: GoProblemsHolder): Boolean
}
