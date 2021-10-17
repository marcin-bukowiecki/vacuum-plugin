/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.strings;

import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Marcin Bukowiecki
 */
public class SprintfStringConcatenationQuickFixDataProvider extends DefaultStringConcatenationQuickFixDataProvider {

  private final SmartPsiElementPointer<PsiElement> start;

  public SprintfStringConcatenationQuickFixDataProvider(@Nullable PsiElement start,
                                                        @NotNull PsiElement elementToReplace,
                                                        @NotNull List<? extends PsiElement> strings,
                                                        @NotNull PsiElement separator) {
    super(elementToReplace, strings, separator);
    this.start = start == null ? null : SmartPointerManager.createPointer(start);
  }

  @Nullable
  @Override
  public SmartPsiElementPointer<PsiElement> getStart() {
    return start;
  }
}
