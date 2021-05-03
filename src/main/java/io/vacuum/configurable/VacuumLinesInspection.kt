/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.configurable

import com.goide.execution.testing.GoTestFinder
import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoFile
import com.goide.psi.GoFunctionDeclaration
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class VacuumLinesInspection : VacuumBaseLocalInspection(), Configurable {

    override fun isSuppressedFor(element: PsiElement): Boolean {
        return GoTestFinder.isTestFile(element.containingFile) || super.isSuppressedFor(element)
    }

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitGoFile(file: GoFile) {
                val firstChild = file.firstChild
                val lastChild = file.lastChild
                val document = VacuumPsiUtils.getDocument(file)
                val lines =
                    document?.getLineNumber(lastChild.endOffset)?.minus(document.getLineNumber(firstChild.startOffset))
                        ?: return

                val maxSourceLines = getMaxSourceLines()

                if (maxSourceLines != -1 && lines > maxSourceLines) {
                    holder
                        .registerProblem(
                            file,
                            VacuumInspectionMessage("Source file exceeds $maxSourceLines lines limit"),
                            ProblemHighlightType.WEAK_WARNING
                        )
                }
            }

            override fun visitFunctionDeclaration(functionDeclaration: GoFunctionDeclaration) {
                val firstChild = functionDeclaration.firstChild
                val lastChild = functionDeclaration.lastChild
                val document = VacuumPsiUtils.getDocument(functionDeclaration)
                val lines =
                    document?.getLineNumber(lastChild.endOffset)?.minus(document.getLineNumber(firstChild.startOffset))
                        ?: return

                val maxFunctionLines = getMaxFunctionLines()

                if (maxFunctionLines != -1 && lines > maxFunctionLines) {
                    holder.registerProblem(
                        getElementToMark(functionDeclaration) ?: return,
                        VacuumInspectionMessage("Function exceeds $maxFunctionLines lines limit")
                    )
                }
            }

            override fun visitMethodDeclaration(methodDeclaration: GoMethodDeclaration) {
                val firstChild = methodDeclaration.firstChild
                val lastChild = methodDeclaration.lastChild
                val document = VacuumPsiUtils.getDocument(methodDeclaration)
                val lines =
                    document?.getLineNumber(lastChild.endOffset)?.minus(document.getLineNumber(firstChild.startOffset))
                        ?: return

                val maxMethodLines = getMaxMethodLines()

                if (maxMethodLines != -1 && lines > maxMethodLines) {
                    holder.registerProblem(
                            getElementToMark(methodDeclaration) ?: return,
                            VacuumInspectionMessage("Method exceeds $maxMethodLines lines limit")
                    )
                }
            }
        }
    }

    private fun getElementToMark(element: GoFunctionOrMethodDeclaration): PsiElement? {
        return if (element.nameIdentifier != null) {
            element.nameIdentifier
        } else {
            element.func
        }
    }
}
