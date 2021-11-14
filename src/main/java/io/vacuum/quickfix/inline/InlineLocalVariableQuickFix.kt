/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.inline

import com.goide.GoTypes
import com.goide.psi.GoExpression
import com.goide.psi.GoReturnStatement
import com.goide.psi.GoSimpleStatement
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.vacuum.quickfix.VacuumBaseLocalQuickFix

/**
 * @author Marcin Bukowiecki
 */
class InlineLocalVariableQuickFix(
  private val toRemoveStmtPtr: SmartPsiElementPointer<GoSimpleStatement>,
  private val rightExpressionsPtr: List<SmartPsiElementPointer<GoExpression>>,
  private val returnStmtPtr: SmartPsiElementPointer<GoReturnStatement>
) :
  VacuumBaseLocalQuickFix("Local variable can be inlined") {

  override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
    val toRemove = toRemoveStmtPtr.element ?: return
    val rightExpressions = rightExpressionsPtr.mapNotNull { it.element }
    if (rightExpressionsPtr.size != rightExpressions.size) return
    val returnStmt = returnStmtPtr.element ?: return

    returnStmt.expressionList.forEachIndexed { index, goExpression ->
      if (index < rightExpressions.size) {
        goExpression.replace(rightExpressions[index])
      } else {
        goExpression.delete()
      }
    }

    val lastChild = returnStmt.lastChild
    if (lastChild is LeafPsiElement && GoTypes.COMMA == lastChild.elementType) {
      lastChild.delete()
    }
    toRemove.delete()
  }
}
