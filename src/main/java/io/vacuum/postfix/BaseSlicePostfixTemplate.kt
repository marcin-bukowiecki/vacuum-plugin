/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix

import com.goide.psi.GoArrayOrSliceType
import com.goide.psi.GoExpression
import com.goide.psi.impl.GoCallExprImpl
import com.goide.psi.impl.GoElementFactory
import com.goide.psi.impl.GoTypeUtil
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement

/**
 * @author Marcin Bukowiecki
 */
abstract class BaseSlicePostfixTemplate(
    id: String,
    name: String,
    example: String,
    provider: PostfixTemplateProvider
) : VacuumBasePostfixTemplate(id, name, example, provider) {

    override fun isApplicable(context: PsiElement, copyDocument: Document, newOffset: Int): Boolean {
        val expr = if (context.parent is GoExpression) {
            context.parent as? GoExpression
        } else if (context.parent?.parent is GoCallExprImpl) {
            context.parent.parent as? GoCallExprImpl
        } else {
            null
        }

        expr?.let {
            val typesOfExpressions = GoTypeUtil.getTypesOfExpressions(mutableListOf(expr))
            if (typesOfExpressions.size == 1 && typesOfExpressions[0] != null) {
                typesOfExpressions[0].getUnderlyingType(expr).let { uType ->
                    if (uType is GoArrayOrSliceType) {
                        return true
                    }
                }
            }
        }

        return false
    }

    override fun expand(context: PsiElement, editor: Editor) {
        getTextForContext(context)?.let { text ->
            val created = GoElementFactory.createComparison(context.project, "len($text) ${getOperator()} 0")

            if (context.parent is GoExpression) {
                context.parent.replace(created)
            } else {
                context.parent?.parent?.let {
                    if (it is GoCallExprImpl) {
                        context.parent.parent.replace(created)
                    }
                }
            }
        }
    }

    abstract fun getOperator(): String
}