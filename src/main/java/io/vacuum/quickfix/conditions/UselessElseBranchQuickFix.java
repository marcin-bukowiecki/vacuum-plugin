/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.conditions;

import com.goide.psi.GoBlock;
import com.goide.psi.GoElseStatement;
import com.goide.psi.GoIfStatement;
import com.goide.psi.GoStatement;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPsiElementPointer;
import io.vacuum.quickfix.VacuumBaseLocalQuickFix;
import io.vacuum.utils.VacuumUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Marcin Bukowiecki
 */
public class UselessElseBranchQuickFix extends VacuumBaseLocalQuickFix {

  private final SmartPsiElementPointer<GoIfStatement> ifStmtPtr;

  private final SmartPsiElementPointer<GoElseStatement> elseStmtPtr;

  public UselessElseBranchQuickFix(@NotNull SmartPsiElementPointer<GoIfStatement> ifStmtPtr,
                                   @NotNull SmartPsiElementPointer<GoElseStatement> elseStmtPtr) {
    super("Remove else branch");
    this.ifStmtPtr = ifStmtPtr;
    this.elseStmtPtr = elseStmtPtr;
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    final GoElseStatement goElseStatement = elseStmtPtr.getElement();
    PsiElement goIfStatement = ifStmtPtr.getElement();
    if (goElseStatement == null) return;
    if (goIfStatement == null) return;

    final GoBlock block = goElseStatement.getBlock();
    if (block == null) return;

    boolean moveCaret = false;
    final List<GoStatement> statementList = block.getStatementList();
    if (!statementList.isEmpty()) {
      final PsiElement parent = goIfStatement.getParent();
      if (parent == null) return;
      goIfStatement = parent.addRangeAfter(statementList.get(0), statementList.get(statementList.size()-1), goIfStatement);
      moveCaret = true;
    }

    goElseStatement.delete();

    if (moveCaret) {
      final PsiElement lastChild = goIfStatement.getLastChild();
      if (lastChild == null) {
        return;
      }
      VacuumUtils.INSTANCE.moveCaretToEnd(project, lastChild);
    }
  }
}
