/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix

import com.goide.psi.GoBlock
import com.goide.psi.GoIfStatement
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.refactoring.suggested.endOffset
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class UselessIfBlockQuickFix : LocalQuickFix {

    override fun getName(): String {
        return "Remove useless if block"
    }

    override fun getFamilyName(): String {
        return "Remove useless if block"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        if (descriptor.psiElement is GoIfStatement) {
            val ifStmt = (descriptor.psiElement as GoIfStatement)
            ifStmt.block?.let { b ->
                if (b.children.isNullOrEmpty()) {
                    b.delete()
                } else {
                    val deepClonePolymorphic = trimFromNewLines(trimFromNewLines(b.children.toList(), "{").reversed(), "}").reversed()
                    if (deepClonePolymorphic.isNotEmpty()) {
                        val parent = ifStmt.parent
                        parent.addRangeBefore(deepClonePolymorphic.first(), deepClonePolymorphic.last(), ifStmt)
                    }
                }
            }
            val parent = ifStmt.parent
            ifStmt.delete()
            if (parent is GoBlock) {
                VacuumPsiUtils.getLastStatement(parent)?.let {
                    lastStmt ->
                    VacuumPsiUtils.getCaret(project)?.moveToOffset(lastStmt.endOffset)
                }
            }
        }
    }

    private fun trimFromNewLines(psiElements: List<PsiElement>, start: String): List<PsiElement> {
        val result: MutableList<PsiElement> = mutableListOf()
        var visitedLeftCurl = false
        var visitedNewLine = false

        for (psiElement in psiElements) {
            if (psiElement.text == start && !visitedLeftCurl) {
                visitedLeftCurl = true
                continue
            }
            if (psiElement is PsiWhiteSpace && !visitedNewLine) {
                visitedNewLine = true
                continue
            }
            result.add(psiElement)
        }

        return result
    }
}
