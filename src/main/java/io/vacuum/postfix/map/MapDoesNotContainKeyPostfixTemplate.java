/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix.map;

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
public class MapDoesNotContainKeyPostfixTemplate extends VacuumBasePostfixTemplate {

    public MapDoesNotContainKeyPostfixTemplate(@NotNull PostfixTemplateProvider provider) {
        super("Vacuum.Map.notContainsKey", "notContainsKey", "if val, ok := dict[key]; !ok { }", provider);
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
        var type = getType(context);
        return type instanceof GoMapTypeImpl;
    }

    @Override
    public void expand(@NotNull PsiElement context, @NotNull Editor editor) {
        var elementToReplace = getElementToReplace(context);
        if (elementToReplace == null) return;

        var text = elementToReplace.getText();
        var templateText = "if $VAL$, ok := " + text + "[$KEY$]; !ok { $END$ }\n";

        var template = new TemplateImpl("Vacuum.AWS.MapNotContains", templateText, "Vacuum.Map");
        setupTemplate(context, elementToReplace, template, editor, List.of(
                new Variable("VAL", "_", "_", true),
                new Variable("KEY", "", "", true)
        ), true);
    }
}
