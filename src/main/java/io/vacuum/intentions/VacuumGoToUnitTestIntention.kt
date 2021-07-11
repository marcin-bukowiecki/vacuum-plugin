/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.intentions

import com.goide.execution.testing.GoTestFinder
import com.goide.psi.GoFile
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumPsiUtils
import io.vacuum.utils.VacuumUtils

/**
 * @author Marcin Bukowiecki
 */
class VacuumGoToUnitTestIntention : VacuumIntentionBaseAction(VacuumBundle.message("vacuum.test.goTo")) {

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file !is GoFile || editor == null) return false
        if (GoTestFinder.isTestFile(file)) return false
        val target = getTargetFunctionOrMethod(editor, file) ?: return false
        if (VacuumPsiUtils.isInner(target)) return false
        return VacuumPsiUtils.unitTestExists(target)
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (editor == null || file == null) return
        val target = getTargetFunctionOrMethod(editor, file) ?: return
        val unitTestFunction = VacuumPsiUtils.getUnitTestFunction(target) ?: return
        val children = unitTestFunction.block?.children
        if (children.isNullOrEmpty()) {
            VacuumUtils.focusEditor(
                project,
                unitTestFunction.containingFile.virtualFile,
                unitTestFunction.func.startOffset
            )
        } else {
            VacuumUtils.focusEditor(
                project,
                unitTestFunction.containingFile.virtualFile,
                children[0].endOffset
            )
        }
    }
}
