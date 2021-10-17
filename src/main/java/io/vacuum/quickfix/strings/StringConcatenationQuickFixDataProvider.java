/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.strings;

import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Marcin Bukowiecki
 */
public interface StringConcatenationQuickFixDataProvider {

  @NotNull SmartPsiElementPointer<PsiElement> getElementToReplace();

  @NotNull List<SmartPsiElementPointer<? extends PsiElement>> getStrings();

  @NotNull SmartPsiElementPointer<PsiElement> getSeparator();

  default @Nullable SmartPsiElementPointer<PsiElement> getEnd() {
    return null;
  }

  default @Nullable SmartPsiElementPointer<PsiElement> getStart() {
    return null;
  }
}
