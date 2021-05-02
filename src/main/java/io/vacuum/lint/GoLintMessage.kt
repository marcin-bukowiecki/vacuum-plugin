/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.lint

import com.goide.psi.GoImportList
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.refactoring.suggested.startOffset

/**
 * @author Marcin Bukowiecki
 */
class GoLintMessage(private val filePath: String,
                    private val row: Int,
                    private val col: Int,
                    private val message: String): LintMessage {

    private var registered = false

    override fun length(): Int {
        return message.length
    }

    override fun startRow(): Int {
        return row
    }

    override fun startCol(): Int {
        return col
    }

    override fun message(): String {
        return message
    }

    override fun toString(): String {
        return "$filePath:$row:$col:$message"
    }

    override fun setRegistered(registered: Boolean) {
        this.registered = registered
    }

    override fun isRegistered(): Boolean {
        return registered
    }

    override fun psiMatches(psiElement: PsiElement): Boolean {
        if (psiElement is PsiWhiteSpace) {
            return false
        }
        if (psiElement is PsiFile) {
            return false
        }
        if (psiElement is GoImportList && psiElement.importDeclarationList.isEmpty()) {
            return false
        }
        return psiElement.containingFile.viewProvider.document?.getLineNumber(psiElement.startOffset)?.let {
                line -> return line + 1 == row
        } ?: return false
    }
}
