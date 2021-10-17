/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.intentions.aws

import com.goide.psi.GoFile
import com.goide.psi.GoType
import com.goide.psi.impl.GoElementFactory
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

  override fun invoke(project: Project, editor: Editor, file: PsiFile) {
    val offset = editor.caretModel.offset
    val element = file.findElementAt(offset - 1) ?: return
    val parent = element.parent

    getReplaceStrategy(parent, editor)?.replace()
  }

  abstract fun getReplaceStrategy(parent: PsiElement, editor: Editor): ReplaceStrategy?

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
