/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix.aws;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Marcin Bukowiecki
 */
public class VacuumAWSPostfixTemplateProvider implements PostfixTemplateProvider {

    private final Set<PostfixTemplate> myTemplates = Set.of(
        new AWSLambdaPostfixTemplate(this)
    );

    @Override
    public @NotNull Set<PostfixTemplate> getTemplates() {
        return myTemplates;
    }

    @Override
    public boolean isTerminalSymbol(char currentChar) {
        return Character.isWhitespace(currentChar);
    }

    @Override
    public void preExpand(@NotNull PsiFile file, @NotNull Editor editor) {

    }

    @Override
    public void afterExpand(@NotNull PsiFile file, @NotNull Editor editor) {

    }

    @Override
    public @NotNull PsiFile preCheck(@NotNull PsiFile copyFile, @NotNull Editor realEditor, int currentOffset) {
        return copyFile;
    }
}
