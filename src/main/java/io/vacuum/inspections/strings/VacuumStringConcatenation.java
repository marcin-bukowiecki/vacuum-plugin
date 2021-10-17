/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.strings;

import com.goide.inspections.core.GoProblemsHolder;
import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiUtil;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import io.vacuum.inspections.VacuumBaseLocalInspection;
import io.vacuum.quickfix.strings.DefaultStringConcatenationQuickFixDataProvider;
import io.vacuum.quickfix.strings.SprintfStringConcatenationQuickFixDataProvider;
import io.vacuum.quickfix.strings.StringsConcatenationQuickFix;
import io.vacuum.settings.VacuumSettingsState;
import io.vacuum.utils.VacuumBundle;
import io.vacuum.utils.VacuumPsiUtils;
import io.vacuum.utils.VacuumUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Marcin Bukowiecki
 */
public class VacuumStringConcatenation extends VacuumBaseLocalInspection {

  private static final String STRINGS = "strings";
  private static final String FMT = "fmt";
  private static final String STRINGS_JOIN = "strings.Join";
  private static final String FMT_SPRINTF = "fmt.Sprintf";
  private static final List<String> POSSIBLE_STRING_ARGS = List.of(
      "%s",
      "%v"
  );

  @Override
  protected @NotNull GoVisitor buildGoVisitor(@NotNull GoProblemsHolder holder,
                                              @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {

      @Override
      public void visitCallExpr(@NotNull GoCallExpr o) {
        final GoReferenceExpression goReferenceExpression = GoPsiUtil.getCallReference(o);
        if (goReferenceExpression == null) return;

        final String referenceText = goReferenceExpression.getText();

        if (STRINGS_JOIN.equals(referenceText) && VacuumPsiUtils.INSTANCE.isImported(o, STRINGS)) {
          handleStringsJoin(o, holder);
        }
        else if (FMT_SPRINTF.equals(referenceText) && VacuumPsiUtils.INSTANCE.isImported(o, FMT)) {
          handleSprintf(o, holder);
        }

        super.visitCallExpr(o);
      }
    };
  }

  private void handleStringsJoin(@NotNull GoCallExpr goCallExpr,
                                 @NotNull GoProblemsHolder holder) {

    final GoArgumentList argumentList = goCallExpr.getArgumentList();
    final List<GoExpression> expressionList = argumentList.getExpressionList();
    if (expressionList.size() != 2) return;

    final GoExpression goExpression = expressionList.get(0);
    final GoType firstType = goExpression.getGoType(null);
    if (goExpression instanceof GoCompositeLit
        && firstType instanceof GoArrayOrSliceType
        && GoTypeUtil.isString(((GoArrayOrSliceType) firstType).getType(), goCallExpr)
        && goExpression.getText().startsWith("[]")) {

      final GoLiteralValue literalValue = ((GoCompositeLit) goExpression).getLiteralValue();
      if (literalValue == null) return;
      if (literalValue.getElementList().size() > VacuumSettingsState.getInstance().getNumberOfStringsForEfficientConcatenation()) return;

      holder.registerProblem(goCallExpr,
          VacuumBundle.INSTANCE.vacuumInspectionMessage("vacuum.strings.concatenation"),
          new StringsConcatenationQuickFix(
              new DefaultStringConcatenationQuickFixDataProvider(
                  goCallExpr,
                  literalValue.getElementList(),
                  expressionList.get(1)
              )
          )
      );
    }
  }

  private void handleSprintf(@NotNull GoCallExpr goCallExpr,
                             @NotNull GoProblemsHolder holder) {

    final Project project = goCallExpr.getProject();
    final GoArgumentList argumentList = goCallExpr.getArgumentList();
    final List<GoExpression> expressionList = argumentList.getExpressionList();
    if (expressionList.size() < 3) return;

    final List<GoExpression> possibleStrings = expressionList.subList(1, expressionList.size());
    if (possibleStrings.size() > VacuumSettingsState.getInstance().getNumberOfStringsForEfficientConcatenation()) return;

    if (!VacuumPsiUtils.INSTANCE.allStrings(possibleStrings)) return;

    final GoExpression stringExpr = expressionList.get(0);
    if (stringExpr instanceof GoStringLiteral) {
      final String decodedText = ((GoStringLiteral) stringExpr).getDecodedText();

      for (String arg : POSSIBLE_STRING_ARGS) {
        final String[] split = decodedText.split(arg);
        if (split.length == 0 || split[0].equals(decodedText)) continue;
        final String delim = VacuumUtils.INSTANCE.detectDelim(split);
        if (delim == null) continue;

        final PsiElement startingElement = tryCreateStart(project, split);

        holder.registerProblem(goCallExpr,
            VacuumBundle.INSTANCE.vacuumInspectionMessage("vacuum.strings.concatenation"),
            new StringsConcatenationQuickFix(
                new SprintfStringConcatenationQuickFixDataProvider(
                    startingElement,
                    goCallExpr,
                    possibleStrings,
                    GoElementFactory.createExpression(project, "\"" + delim + "\"")
                )
            )
        );
        break;
      }
    }
  }

  private static @Nullable PsiElement tryCreateStart(final Project project, final String[] split) {
    if (split.length == 0) return null;
    if (split[0].isEmpty()) return null;
    return GoElementFactory.createStringLiteral(project, "\"" + split[0] + "\"");
  }
}
