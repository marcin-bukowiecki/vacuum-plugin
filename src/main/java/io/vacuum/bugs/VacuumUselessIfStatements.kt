/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.bugs

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoBinaryExpr
import com.goide.psi.GoElseStatement
import com.goide.psi.GoIfStatement
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.keys.UserDataKeys
import io.vacuum.quickfix.UselessIfBlockQuickFix
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumUtils.isBool

/**
 * Useless if statements e.g.
 *
 * if true {
 *    expr
 * }
 *
 * if false {
 *    expr
 * }
 *
 * @author Marcin Bukowiecki
 */
class VacuumUselessIfStatements : VacuumBaseLocalInspection() {

  private val exceptions = setOf("*", "+", "<<", "=")

  override fun buildGoVisitor(
    holder: GoProblemsHolder,
    session: LocalInspectionToolSession
  ): GoVisitor {

    return object : GoVisitor() {

      override fun visitIfStatement(goIfStatement: GoIfStatement) {
        if (goIfStatement.parent is GoElseStatement) return
        if (goIfStatement.block != null && goIfStatement.elseStatement != null) return
        if (checkIfChildrenMarked(goIfStatement)) return

        goIfStatement.condition?.let { condition ->
          if (condition.isConstant && condition.text.isBool()) {
            goIfStatement.putUserData(UserDataKeys.USELESS_IF_KEY, true)
            holder.registerProblem(
              goIfStatement,
              VacuumBundle.vacuumInspectionMessage("vacuum.ifStatement.useless"),
              ProblemHighlightType.WARNING,
              UselessIfBlockQuickFix()
            )
          }
        }
      }

      override fun visitBinaryExpr(o: GoBinaryExpr) {
        if (o.operator == null) return
        val left = o.left
        val right = o.right ?: return
        if (left.text == right.text && (o.operator as PsiElement).text !in exceptions) {
          holder.registerProblem(
            o,
            VacuumBundle.vacuumInspectionMessage("vacuum.binary.sameExpression"),
            ProblemHighlightType.WARNING
          )
        }
      }
    }
  }

  private fun checkIfChildrenMarked(ifStatement: GoIfStatement): Boolean {
    return ifStatement.block?.children?.any { ch -> ch.getUserData(UserDataKeys.USELESS_IF_KEY) == true } ?: false
  }
}
