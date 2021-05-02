/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.intentions

import com.goide.GoTypes
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType

/**
 * @author Marcin Bukowiecki
 */
abstract class VacuumIntentionBaseAction(private val name: String) : IntentionAction {

    override fun startInWriteAction() = false

    override fun getText(): String {
        return name
    }

    override fun getFamilyName(): String {
        return name
    }

    open fun getTargetFunctionOrMethod(editor: Editor, file: PsiFile): GoFunctionOrMethodDeclaration? {
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        return if (element.elementType == GoTypes.IDENTIFIER && element?.parent is GoFunctionOrMethodDeclaration) {
            element.parent as GoFunctionOrMethodDeclaration
        } else {
            null
        }
    }
}
