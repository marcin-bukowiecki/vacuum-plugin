/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.error

import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPsiElementPointer
import io.vacuum.quickfix.VacuumBaseLocalQuickFix
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class OverwroteErrorQuickfix(
    private val errorText: String,
    private val appendAfterRef: SmartPsiElementPointer<PsiElement>
) : VacuumBaseLocalQuickFix(VacuumBundle.message("vacuum.err.handle")) {

    override fun applyFix(project: Project, problemDescriptor: ProblemDescriptor) {
        val createIfStatement = GoElementFactory.createIfStatement(project, "$errorText != nil", "", null)
        appendAfterRef.element?.let {
            it.parent.addAfter(createIfStatement, it)
        }
    }
}
