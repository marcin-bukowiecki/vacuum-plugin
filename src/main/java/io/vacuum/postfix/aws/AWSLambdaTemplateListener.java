/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix.aws;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingListener;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Marcin Bukowiecki
 */
public class AWSLambdaTemplateListener implements TemplateEditingListener {

    private final SmartPsiElementPointer<PsiElement> addedHandlerTypePtr;
    private final SmartPsiElementPointer<PsiElement> addedMainFunctionPtr;

    public AWSLambdaTemplateListener(@Nullable SmartPsiElementPointer<PsiElement> addedHandlerTypePtr,
                                     @Nullable SmartPsiElementPointer<PsiElement> addedMainFunctionPtr) {

        this.addedHandlerTypePtr = addedHandlerTypePtr;
        this.addedMainFunctionPtr = addedMainFunctionPtr;
    }

    @Override
    public void beforeTemplateFinished(@NotNull TemplateState state, Template template) {

    }

    @Override
    public void templateFinished(@NotNull Template template, boolean brokenOff) {

    }

    @Override
    public void templateCancelled(Template template) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            PsiElement element;
            if (addedHandlerTypePtr != null && (element = addedHandlerTypePtr.getElement()) != null) {
                element.delete();
            }

            if (addedMainFunctionPtr != null && (element = addedMainFunctionPtr.getElement()) != null) {
                element.delete();
            }
        });
    }

    @Override
    public void currentVariableChanged(@NotNull TemplateState templateState, Template template, int oldIndex, int newIndex) {

    }

    @Override
    public void waitingForInput(Template template) {

    }
}
