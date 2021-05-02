/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.lint

import com.intellij.psi.PsiElement

/**
 * @author Marcin Bukowiecki
 */
interface LintMessage {

    fun length(): Int

    fun startRow(): Int

    fun startCol(): Int

    fun message(): String

    fun psiMatches(psiElement: PsiElement): Boolean

    fun isRegistered(): Boolean

    fun setRegistered(registered: Boolean)
}
