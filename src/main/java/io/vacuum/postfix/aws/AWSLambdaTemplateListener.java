/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix.aws;

import com.goide.psi.GoFile;
import com.goide.psi.GoImportSpec;
import com.goide.psi.GoTypeDeclaration;
import com.goide.psi.GoTypeSpec;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingListener;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPsiElementPointer;
import io.vacuum.utils.VacuumPsiUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author Marcin Bukowiecki
 */
public class AWSLambdaTemplateListener implements TemplateEditingListener {

    private final GoFile goFile;
    private final Editor editor;
    private final SmartPsiElementPointer<PsiElement> addedHandlerTypePtr;
    private final SmartPsiElementPointer<PsiElement> addedMainFunctionPtr;

    public AWSLambdaTemplateListener(@NotNull GoFile goFile,
                                     @NotNull Editor editor,
                                     @Nullable SmartPsiElementPointer<PsiElement> addedHandlerTypePtr,
                                     @Nullable SmartPsiElementPointer<PsiElement> addedMainFunctionPtr) {

        this.goFile = goFile;
        this.editor = editor;
        this.addedHandlerTypePtr = addedHandlerTypePtr;
        this.addedMainFunctionPtr = addedMainFunctionPtr;
    }

    @Override
    public void beforeTemplateFinished(@NotNull TemplateState state, Template template) {

    }

    @Override
    public void templateFinished(@NotNull Template template, boolean brokenOff) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            final GoImportSpec context = VacuumPsiUtils.INSTANCE.findImport(goFile, "context");
            if (context == null) {
                goFile.addImport("context", null);
            }
        });
    }

    @Override
    public void templateCancelled(Template template) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            final Document document = editor.getDocument();
            final PsiDocumentManager manager = PsiDocumentManager.getInstance(goFile.getProject());
            manager.commitDocument(document);

            PsiElement element;

            if (addedHandlerTypePtr != null && (element = addedHandlerTypePtr.getElement()) != null) {
                if (element instanceof GoTypeDeclaration) {
                    final GoTypeDeclaration typeDeclaration = (GoTypeDeclaration) element;
                    final List<GoTypeSpec> typeSpecList = typeDeclaration.getTypeSpecList();
                    if (CollectionUtils.isNotEmpty(typeSpecList)) {
                        final GoTypeSpec goTypeSpec = typeSpecList.get(0);
                        goTypeSpec.getMethods().stream()
                                .filter(m -> Objects.equals(m.getName(), "handle"))
                                .findFirst()
                                .ifPresent(PsiElement::delete);
                    }
                }

                element.delete();
            }

            if (addedMainFunctionPtr != null && (element = addedMainFunctionPtr.getElement()) != null) {
                element.delete();
            }

            manager.commitDocument(document);
        });
    }

    @Override
    public void currentVariableChanged(@NotNull TemplateState templateState, Template template, int oldIndex, int newIndex) {

    }

    @Override
    public void waitingForInput(Template template) {

    }
}
