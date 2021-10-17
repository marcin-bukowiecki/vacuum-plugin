/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.conditions;

import com.goide.inspections.core.GoProblemsHolder;
import com.goide.psi.*;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import io.vacuum.inspections.VacuumBaseLocalInspection;
import io.vacuum.quickfix.conditions.UselessElseBranchQuickFix;
import io.vacuum.utils.VacuumBundle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Marcin Bukowiecki
 */
public class VacuumUselessElseBranch extends VacuumBaseLocalInspection {

  @Override
  protected @NotNull GoVisitor buildGoVisitor(@NotNull GoProblemsHolder holder,
                                              @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {

      @Override
      public void visitElseStatement(@NotNull GoElseStatement stmt) {
        if (stmt.getIfStatement() != null) return;

        final PsiElement parent = stmt.getParent();
        if (parent instanceof GoIfStatement) {
          final GoIfStatement goIfStatement = (GoIfStatement) parent;

          final GoBlock block = goIfStatement.getBlock();
          if (block == null) return;

          final List<GoStatement> statementList = block.getStatementList();
          if (statementList.isEmpty()) return;

          final GoStatement last = statementList.get(statementList.size() - 1);
          if (last instanceof GoReturnStatement) {
            holder.registerProblem(
                stmt.getElse(),
                VacuumBundle.INSTANCE.vacuumInspectionMessage("vacuum.conditions.uselessElseBranch"),
                new UselessElseBranchQuickFix(
                    SmartPointerManager.createPointer(goIfStatement),
                    SmartPointerManager.createPointer(stmt)
                )
            );
          }
        }
      }
    };
  }
}
