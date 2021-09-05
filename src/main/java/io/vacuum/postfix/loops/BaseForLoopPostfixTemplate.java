/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix.loops;

import com.goide.psi.GoType;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import io.vacuum.postfix.VacuumBasePostfixTemplate;
import org.jetbrains.annotations.NotNull;

/**
 * @author Marcin Bukowiecki
 */
abstract class BaseForLoopPostfixTemplate extends VacuumBasePostfixTemplate {

    public BaseForLoopPostfixTemplate(@NotNull String id,
                                      @NotNull String name,
                                      @NotNull String example,
                                      @NotNull PostfixTemplateProvider provider) {
        super(id, name, example, provider);
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
        final GoType type = getType(context);
        return GoTypeUtil.isBoolean(type, context);
    }
}
