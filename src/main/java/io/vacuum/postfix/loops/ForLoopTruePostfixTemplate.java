/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix.loops;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * @author Marcin Bukowiecki
 */
public class ForLoopTruePostfixTemplate extends BaseForLoopPostfixTemplate {

    public ForLoopTruePostfixTemplate(@NotNull PostfixTemplateProvider provider) {
        super("Vacuum.Loop.forLoopTrue", "for true", "for true { }", provider);
    }

    @Override
    public void expand(@NotNull PsiElement context, @NotNull Editor editor) {
        var elementToReplace = getElementToReplace(context);
        if (elementToReplace == null) return;
        var text = getTextForContext(context);
        var templateText = "for " + text + " { \n$END$ }\n";
        var template = new TemplateImpl("Vacuum.Loop.forLoopTrue", templateText, "Vacuum.Loop");
        setupTemplate(context, elementToReplace, template, editor, Collections.emptyList(), true);
    }
}
