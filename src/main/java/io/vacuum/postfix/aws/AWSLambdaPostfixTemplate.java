/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix.aws;

import com.goide.GoTypes;
import com.goide.psi.GoFile;
import com.goide.psi.GoPackageClause;
import com.goide.psi.GoTypeDeclaration;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiUtil;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.Variable;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import io.vacuum.postfix.VacuumBasePostfixTemplate;
import io.vacuum.utils.VacuumPsiUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Marcin Bukowiecki
 */
public class AWSLambdaPostfixTemplate extends VacuumBasePostfixTemplate {

    private final Logger log = LoggerFactory.getLogger(AWSLambdaPostfixTemplate.class);

    private final String myLoadedLambdaTemplate;

    public AWSLambdaPostfixTemplate(@NotNull PostfixTemplateProvider provider) {
        super("Vacuum.AWS.lambda", "lambda", "", provider);
        this.myLoadedLambdaTemplate = loadTemplate();
    }

    private String loadTemplate() {
        try {
            final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("customTemplates/lambda1.go");
            if (resourceAsStream == null) {
                log.info("Could not open template file: lambda1.go");
            } else {
                return IOUtils.toString(resourceAsStream, Charset.defaultCharset());
            }
        } catch (Exception ex) {
            log.error("Exception while loading lambda template", ex);
        }
        return "";
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
        final PsiElement parent = context.getParent();
        if (context instanceof LeafPsiElement && ((LeafPsiElement) context).getElementType() == GoTypes.IDENTIFIER) {
            return parent instanceof GoPackageClause;
        }
        return GoPsiUtil.isWhiteSpaceOrCommentOrEmpty(context) && parent instanceof GoFile;
    }

    @Override
    public void expand(@NotNull PsiElement context, @NotNull Editor editor) {
        if (myLoadedLambdaTemplate.isEmpty()) {
            log.info("AWS lambda template is empty");
            return;
        }

        var goFile = getGoFile(context);
        if (goFile == null) return;

        if (context instanceof GoPackageClause) {
            goFile.add(GoElementFactory.createNewLine(context.getProject()));
        }

        var addedHandlerTypeOrNull = createHandlerIfNotExist(goFile, context);
        var mainOrNull = createMainIfNotExist(goFile, context);

        PsiDocumentManager
                .getInstance(context.getProject())
                .doPostponedOperationsAndUnblockDocument(editor.getDocument());

        var template = new TemplateImpl("Vacuum.AWS.lambda", myLoadedLambdaTemplate, "Vacuum.AWS");
        template.setToIndent(true);
        template.setToReformat(true);
        template.addVariable(new Variable("LAMBDA_PARAMETERS", "", "", true));
        template.addVariable(new Variable("RETURN_TYPES", "", "", true));

        TemplateManager
                .getInstance(context.getProject())
                .startTemplate(
                        editor,
                        template,
                        new AWSLambdaTemplateListener(addedHandlerTypeOrNull, mainOrNull)
                );
    }

    @Nullable
    private SmartPsiElementPointer<PsiElement> createHandlerIfNotExist(GoFile goFile, PsiElement context) {
        final List<PsiElement> types = Arrays.stream(goFile.getChildren())
                .filter(it -> it instanceof GoTypeDeclaration)
                .collect(Collectors.toList());
        if (!types.isEmpty()) return null;

        final GoTypeDeclaration created = VacuumPsiUtils.INSTANCE.createStruct("handler", context);
        return SmartPointerManager.createPointer(goFile.add(created));
    }

    @Nullable
    private SmartPsiElementPointer<PsiElement> createMainIfNotExist(GoFile goFile, PsiElement context) {
        var mainOrNull = goFile.getFunctions().stream().filter(GoPsiUtil::isMainFunction).findFirst();
        if (mainOrNull.isPresent()) return null;

        var dummyFile = GoElementFactory.createFileFromText(
                context.getProject(),
                "package foo\nfunc main() {\n" +
                        "\th := handler{}\n" +
                        "\tlambda.Start(h.handle)\n" +
                        "}");
        return dummyFile.getFunctions("main").stream()
                .findFirst()
                .map(mainFunction -> SmartPointerManager.createPointer(goFile.add(mainFunction)))
                .orElse(null);
    }
}
