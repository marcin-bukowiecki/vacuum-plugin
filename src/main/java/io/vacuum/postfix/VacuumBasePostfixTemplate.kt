/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix

import com.goide.psi.GoExpression
import com.goide.psi.GoFile
import com.goide.psi.GoType
import com.goide.psi.impl.GoCallExprImpl
import com.goide.psi.impl.GoTypeUtil
import com.intellij.codeInsight.template.Template
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.Variable
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement

/**
 * @author Marcin Bukowiecki
 */
abstract class VacuumBasePostfixTemplate(
    id: String,
    name: String,
    example: String,
    provider: PostfixTemplateProvider
) : PostfixTemplate(id, name, example, provider) {

    fun getGoFile(context: PsiElement): GoFile? {
        return context.containingFile as? GoFile
    }

    fun setupTemplate(context: PsiElement,
                      elementToReplace: PsiElement,
                      template: Template,
                      editor: Editor,
                      variables: List<Variable> = emptyList(),
                      toReformat: Boolean = true) {

        template.setToIndent(true)
        template.isToReformat = toReformat
        variables.forEach { template.addVariable(it) }

        elementToReplace.delete()
        PsiDocumentManager
            .getInstance(context.project)
            .doPostponedOperationsAndUnblockDocument(editor.document)

        TemplateManager.getInstance(context.project).startTemplate(editor, template)
    }

    fun getType(context: PsiElement): GoType? {
        getElementToReplace(context)?.let { element ->
            (element as? GoExpression)?.let { expr ->
                GoTypeUtil.getTypesOfExpressions(mutableListOf(expr)).let {
                    return if (it.isEmpty()) {
                        null
                    } else {
                        it[0]
                    }
                }
            }
        }

        return null
    }

    fun getTextForContext(context: PsiElement): String? {
        return if (context.parent is GoExpression) {
            (context.parent as? GoExpression)?.text
        } else if (context.parent?.parent is GoCallExprImpl) {
            (context.parent.parent as? GoCallExprImpl)?.text
        } else {
            null
        }
    }

    fun getElementToReplace(context: PsiElement): PsiElement? {
        if (context.parent is GoExpression) {
            return context.parent
        } else {
            context.parent?.parent?.let {
                if (it is GoCallExprImpl) {
                    return context.parent.parent
                }
            }
        }

        return null
    }
}