/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.intentions.aws

import com.goide.psi.GoExpression
import com.goide.psi.GoFile
import com.goide.psi.GoStringLiteral
import com.goide.psi.GoType
import com.goide.psi.impl.GoTypeUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumToAWSStringIntention : BaseToAWSIntention(VacuumBundle.message("vacuum.aws.string.create")) {

  override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
    if (file !is GoFile) return false

    val offset = editor.caretModel.offset
    val element = file.findElementAt(offset - 1) ?: return false
    val parent = element.parent

    if (parent is GoStringLiteral) return true

    if (parent is GoExpression) {
      val typesOfExpressions = GoTypeUtil.getTypesOfExpressions(mutableListOf(parent))
      typesOfExpressions[0]?.let { t ->
        if (typeSupported(t, element)) {
          return true
        }
      }
    }

    return false
  }

  override fun typeSupported(type: GoType, element: PsiElement): Boolean {
    return GoTypeUtil.isString(type, element)
  }

  override fun invoke(project: Project, editor: Editor, file: PsiFile) {
    val offset = editor.caretModel.offset
    val element = file.findElementAt(offset - 1) ?: return
    val parent = element.parent

    getReplaceStrategy(parent, editor)?.replace()
  }

  override fun getReplaceStrategy(parent: PsiElement, editor: Editor): ReplaceStrategy? {
    return when (parent) {
      is GoStringLiteral -> {
        StringReplaceStrategy(parent, editor)
      }
      is GoExpression -> {
        StringReplaceStrategy(parent, editor)
      }
      else -> {
        null
      }
    }
  }
}


/**
 * @author Marcin Bukowiecki
 */
class StringReplaceStrategy(
  private val expression: GoExpression,
  override val editor: Editor
) : ReplaceStrategy {

  override val functionExpr: String
    get() = "aws.String(1)"

  override fun replace() {
    replace( expression)
  }
}


