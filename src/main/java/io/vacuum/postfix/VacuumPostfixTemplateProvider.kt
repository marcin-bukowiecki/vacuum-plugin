/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import io.vacuum.postfix.map.MapContainsKeyPostfixTemplate
import io.vacuum.postfix.map.MapDoesNotContainKeyPostfixTemplate


/**
 * @author Marcin Bukowiecki
 */
class VacuumPostfixTemplateProvider : PostfixTemplateProvider {

    private val myTemplates = mutableSetOf<PostfixTemplate>(
        EmptySlicePostfixTemplate(this),
        NotEmptySlicePostfixTemplate(this),
        MapContainsKeyPostfixTemplate(this),
        MapDoesNotContainKeyPostfixTemplate(this)
    )

    override fun getTemplates(): MutableSet<PostfixTemplate> = myTemplates

    override fun isTerminalSymbol(currentChar: Char): Boolean {
        return currentChar == '.' || currentChar == '!' || currentChar == '&' || currentChar == '*'
    }

    override fun preExpand(file: PsiFile, editor: Editor) {

    }

    override fun afterExpand(file: PsiFile, editor: Editor) {

    }

    override fun preCheck(copyFile: PsiFile, realEditor: Editor, currentOffset: Int): PsiFile {
        return copyFile
    }
}