/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.intentions.aws

import com.goide.psi.GoExpression
import com.goide.psi.GoFile
import com.goide.psi.GoType
import com.goide.psi.impl.GoElementFactory
import com.goide.psi.impl.GoTypeUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.vacuum.intentions.VacuumIntentionBaseAction
import io.vacuum.utils.VacuumPsiUtils

const val awwImportString = "github.com/aws/aws-sdk-go/aws"

/**
 * @author Marcin Bukowiecki
 */
abstract class BaseToAWSIntention(name: String) : VacuumIntentionBaseAction(name) {

  abstract val functionExpr: String

  override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
    if (file !is GoFile) return false

    val offset = editor.caretModel.offset
    val element = file.findElementAt(offset - 1) ?: return false
    val parent = element.parent

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

  override fun invoke(project: Project, editor: Editor, file: PsiFile) {
    val offset = editor.caretModel.offset
    val element = file.findElementAt(offset - 1) ?: return
    val parent = element.parent

    getReplaceStrategy(parent, editor)?.replace()
  }

  open fun getReplaceStrategy(parent: PsiElement, editor: Editor): ReplaceStrategy? = BaseReplaceStrategy(parent, editor, functionExpr)

  abstract fun typeSupported(type: GoType, element: PsiElement): Boolean
}


/**
 * @author Marcin Bukowiecki
 */
interface ReplaceStrategy {

  val editor: Editor
  val functionExpr: String

  fun replace()

  fun replace(toReplace: PsiElement) {
    val project = toReplace.project
    val callExpression = GoElementFactory.createCallExpression(project, functionExpr)
    val firstArg = callExpression.argumentList.expressionList[0]

    ApplicationManager.getApplication().runWriteAction {
      firstArg.replace(toReplace)
      val containingFile = toReplace.containingFile
      toReplace.replace(callExpression)
      (containingFile as? GoFile)?.let { goFile ->
        if (!VacuumPsiUtils.containsImport(goFile, awwImportString)) {
          goFile.addImport(awwImportString, null)
        }
      }
      PsiDocumentManager.getInstance(project).commitDocument(editor.document)
    }
  }
}

/**
 * @author Marcin Bukowiecki
 */
class BaseReplaceStrategy(private val toReplace: PsiElement,
                          override val editor: Editor,
                          override val functionExpr: String): ReplaceStrategy {
  override fun replace() {
    replace(toReplace)
  }
}
