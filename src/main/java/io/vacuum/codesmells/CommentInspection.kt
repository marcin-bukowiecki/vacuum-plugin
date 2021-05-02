/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiWhiteSpace

/**
 * @author Marcin Bukowiecki
 */
class CommentInspection : LocalInspectionTool() {

    override fun isSuppressedFor(element: PsiElement): Boolean {
        return element is PsiComment
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GoVisitor() {

            override fun visitComment(comment: PsiComment) {
                if (comment.tokenType.toString() == "GO_MULTILINE_COMMENT") {
                    if (comment.children.none { e -> e !is PsiWhiteSpace }) {
                        holder.registerProblem(comment, "Empty multi line comment")
                    }
                }
            }
        }
    }
}
