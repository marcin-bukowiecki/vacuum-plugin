/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.psi.GoStatement
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class StatementRegister {

    private var register = mutableMapOf<RegisterKey, List<SmartPsiElementPointer<PsiElement>>>()

    fun addStatement(statement: GoStatement): Boolean {
        val key = createKey(statement) ?: return false
        var onSameLine = false

        register[key].let { list ->
            if (list == null) {
                register[key] = listOf(SmartPointerManager.createPointer(statement))
            } else {
                onSameLine = true
                register[key] = list + listOf(SmartPointerManager.createPointer(statement))
            }
        }

        return onSameLine
    }

    fun findStatementOnSameLine(statement: GoStatement): List<SmartPsiElementPointer<PsiElement>> {
        return register[createKey(statement)] ?: return emptyList()
    }

    private fun createKey(statement: GoStatement): RegisterKey? {
        val document = VacuumPsiUtils.getDocument(statement) ?: return null
        val start = document.getLineNumber(statement.startOffset)
        val end = document.getLineNumber(statement.endOffset)
        return RegisterKey(start, end)
    }
}

