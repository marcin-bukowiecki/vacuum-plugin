/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix

import com.goide.psi.GoShortVarDeclaration
import com.goide.psi.GoVarDefinition
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.refactoring.suggested.endOffset
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class UnhandledErrorQuickfix(
    private val ptr: SmartPsiElementPointer<GoShortVarDeclaration>,
    private val errPtr: SmartPsiElementPointer<GoVarDefinition>
) :
    VacuumBaseLocalQuickFix(VacuumBundle.message("vacuum.err.handle")) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val ifStmt = GoElementFactory.createIfStatement(project, "err != nil", "", null)
        val parent = ptr.element?.parent ?: return
        val nl = parent.addAfter(GoElementFactory.createNewLine(project), ptr.element)
        val addedStmt = parent.addAfter(ifStmt, nl)
        val declaration = GoElementFactory.createVarDeclaration(project, "err = foo")
        errPtr.element?.replace(declaration.varSpecList.first().varDefinitionList.first() ?: return)
        VacuumPsiUtils
            .getCaret(project)?.moveToOffset(addedStmt.children.last().children.firstOrNull()?.endOffset ?: return)
    }
}