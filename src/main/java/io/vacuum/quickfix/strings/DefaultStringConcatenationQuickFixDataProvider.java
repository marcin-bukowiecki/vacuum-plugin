/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.strings;

import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Marcin Bukowiecki
 */
public class DefaultStringConcatenationQuickFixDataProvider implements StringConcatenationQuickFixDataProvider {

  private final SmartPsiElementPointer<PsiElement> elementToReplace;

  private final List<SmartPsiElementPointer<? extends PsiElement>> strings;

  private final SmartPsiElementPointer<PsiElement> separator;

  public DefaultStringConcatenationQuickFixDataProvider(@NotNull PsiElement elementToReplace,
                                                        @NotNull List<? extends PsiElement> strings,
                                                        @NotNull PsiElement separator) {
    this.elementToReplace = SmartPointerManager.createPointer(elementToReplace);
    this.strings = strings.stream().map(SmartPointerManager::createPointer).collect(Collectors.toList());
    this.separator = SmartPointerManager.createPointer(separator);
  }

  @Override
  public @NotNull SmartPsiElementPointer<PsiElement> getElementToReplace() {
    return elementToReplace;
  }

  @Override
  public @NotNull List<SmartPsiElementPointer<? extends PsiElement>> getStrings() {
    return strings;
  }

  @Override
  public @NotNull SmartPsiElementPointer<PsiElement> getSeparator() {
    return separator;
  }
}
