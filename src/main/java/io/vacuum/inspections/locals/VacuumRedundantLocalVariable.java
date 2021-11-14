/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.locals;

import com.goide.inspections.core.GoProblemsHolder;
import com.goide.psi.*;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
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
import java.util.stream.Collectors;

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

          final GoFunctionOrMethodDeclaration functionOrMethod = getFunctionOrMethod(stmt);
          if (functionOrMethod == null) return;

          final List<GoType> returnTypes = GoTypeUtil.getListOfTypesFromTypeList(functionOrMethod.getResultType());
          final List<GoExpression> returnExpressionLists = ((GoReturnStatement) nextSibling).getExpressionList();
          if (returnExpressionLists.isEmpty()) return;
          if (returnTypes.size() != returnExpressionLists.size()) return;

          final List<GoVarDefinition> varDefinitionList = shortVarDeclaration.getVarDefinitionList();
          int varIndex = 0;

          for (GoExpression goExpression : returnExpressionLists) {
            final PsiReference reference = goExpression.getReference();
            if (reference == null) return;
            final GoVarDefinition goVarDefinition = varDefinitionList.get(varIndex);
            if (goVarDefinition == null) return;

            if (!checkReference(reference, goVarDefinition)) {
              return;
            }

            varIndex++;
          }

          holder.registerProblem(
              shortVarDeclaration,
              varDefinitionList.size() == 1 ?
                  VacuumBundle.INSTANCE.vacuumInspectionMessage("vacuum.local.inlineSingle") :
                  VacuumBundle.INSTANCE.vacuumInspectionMessage("vacuum.local.inlineMulti"),
              new InlineLocalVariableQuickFix(
                  SmartPointerManager.createPointer(stmt),
                  shortVarDeclaration.getRightExpressionsList().stream().map(SmartPointerManager::createPointer).collect(Collectors.toList()),
                  SmartPointerManager.createPointer((GoReturnStatement) nextSibling)
              )
          );
        }
      }
    };
  }

  private static boolean checkReference(@NotNull PsiReference reference, @NotNull GoVarDefinition varDefinition) {
    final PsiElement resolved = reference.resolve();
    if (resolved instanceof GoVarDefinition) {
      return varDefinition.isEquivalentTo(resolved);
    }
    return false;
  }

  private static @Nullable GoFunctionOrMethodDeclaration getFunctionOrMethod(@NotNull GoStatement stmt) {
    PsiElement parent = stmt.getParent();

    while (parent != null) {
      if (parent instanceof GoFunctionOrMethodDeclaration) {
        return (GoFunctionOrMethodDeclaration) parent;
      }
      parent = parent.getParent();
    }

    return null;
  }
}
