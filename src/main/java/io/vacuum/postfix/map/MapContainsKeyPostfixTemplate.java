/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix.map;

import com.goide.psi.GoType;
import com.goide.psi.impl.GoMapTypeImpl;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.Variable;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import io.vacuum.postfix.VacuumBasePostfixTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Marcin Bukowiecki
 */
public class MapContainsKeyPostfixTemplate extends VacuumBasePostfixTemplate {

    public MapContainsKeyPostfixTemplate(@NotNull PostfixTemplateProvider provider) {
        super("Vacuum.Map.containsKey", "containsKey", "if val, ok := dict[key]; ok { }", provider);
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
        final GoType type = getType(context);
        return type instanceof GoMapTypeImpl;
    }

    @Override
    public void expand(@NotNull PsiElement context, @NotNull Editor editor) {
        final PsiElement elementToReplace = getElementToReplace(context);
        if (elementToReplace == null) return;

        final String text = elementToReplace.getText();

        var templateText = "if $VAL$, ok := " + text + "[$KEY$]; ok { $END$ }\n";
        var template = new TemplateImpl("Vacuum.AWS.MapContains", templateText, "Vacuum.Map");
        setupTemplate(context, elementToReplace, template, editor, List.of(
                new Variable("VAL", "_", "_", true),
                new Variable("KEY", "", "", true)
        ), true);
    }
}
