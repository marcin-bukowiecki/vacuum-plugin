/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.locals;

import com.goide.inspections.core.GoProblemsHolder;
import com.goide.psi.*;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.SmartPointerManager;
import io.vacuum.inspections.VacuumBaseLocalInspection;
import io.vacuum.quickfix.inline.InlineLocalVariableQuickFix;
import io.vacuum.utils.VacuumBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Marcin Bukowiecki
 */
public class VacuumRedundantLocalVariable extends VacuumBaseLocalInspection {

  @Override
  protected @NotNull GoVisitor buildGoVisitor(@NotNull GoProblemsHolder holder,
                                              @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {

      @Override
      public void visitSimpleStatement(@NotNull GoSimpleStatement stmt) {
        PsiElement nextSibling = stmt.getNextSibling();
        while (nextSibling instanceof PsiWhiteSpace) {
          nextSibling = nextSibling.getNextSibling();
        }

        if (nextSibling instanceof GoReturnStatement) {
          final GoShortVarDeclaration shortVarDeclaration = stmt.getShortVarDeclaration();
          if (shortVarDeclaration == null) return;

          final List<GoExpression> returnExpressionLists = ((GoReturnStatement) nextSibling).getExpressionList();
          if (returnExpressionLists.isEmpty()) return;

          final List<GoVarDefinition> varDefinitionList = shortVarDeclaration.getVarDefinitionList();
          for (GoExpression goExpression : returnExpressionLists) {
            final PsiReference reference = goExpression.getReference();
            if (reference == null) return;

            final Pair<Integer, GoVarDefinition> checkResult = checkReference(reference, varDefinitionList);
            if (checkResult != null) {
              final Integer expressionIndex = checkResult.getFirst();
              final List<GoExpression> rightExpressionsList = shortVarDeclaration.getRightExpressionsList();
              if (expressionIndex >= rightExpressionsList.size()) return;

              holder.registerProblem(
                  goExpression,
                  VacuumBundle.INSTANCE.vacuumInspectionMessage("vacuum.local.inline"),
                  new InlineLocalVariableQuickFix(
                      SmartPointerManager.createPointer(goExpression),
                      SmartPointerManager.createPointer(checkResult.getSecond()),
                      SmartPointerManager.createPointer(shortVarDeclaration),
                      SmartPointerManager.createPointer(rightExpressionsList.get(expressionIndex))
                  )
              );
              break;
            }
          }
        }
      }
    };
  }

  private static @Nullable Pair<Integer, GoVarDefinition> checkReference(@NotNull PsiReference reference,
                                                                         @NotNull List<GoVarDefinition> varDefinitionList) {
    final PsiElement resolved = reference.resolve();
    if (resolved instanceof GoVarDefinition) {
      int i = 0;
      for (GoVarDefinition goVarDefinition : varDefinitionList) {
        if (goVarDefinition.isEquivalentTo(resolved)) {
          return Pair.pair(i, goVarDefinition);
        }
        i++;
      }
    }

    return null;
  }
}
