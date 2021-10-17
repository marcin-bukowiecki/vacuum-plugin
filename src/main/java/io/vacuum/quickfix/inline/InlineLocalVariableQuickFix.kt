/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.inline

import com.goide.psi.GoExpression
import com.goide.psi.GoShortVarDeclaration
import com.goide.psi.GoVarDefinition
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.quickfix.VacuumBaseLocalQuickFix

/**
 * @author Marcin Bukowiecki
 */
class InlineLocalVariableQuickFix(
  private val expressionToBeReplacedPtr: SmartPsiElementPointer<GoExpression>,
  private val varToInlinedPtr: SmartPsiElementPointer<GoVarDefinition>,
  private val varDeclarationPtr: SmartPsiElementPointer<GoShortVarDeclaration>,
  private val expressionToAddPtr: SmartPsiElementPointer<GoExpression>
) :
  VacuumBaseLocalQuickFix("Local variable can be inlined") {

  override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
    val exprToReplace = expressionToBeReplacedPtr.element ?: return
    val exprToAdd = expressionToAddPtr.element ?: return
    val varToInlined = varToInlinedPtr.element ?: return
    val valDeclaration = varDeclarationPtr.element ?: return

    exprToReplace.replace(exprToAdd)
    valDeclaration.deleteDefinition(varToInlined)
  }
}
