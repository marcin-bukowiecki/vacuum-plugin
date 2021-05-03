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
import io.vacuum.dialogs.GoCreateTestDialog
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumPsiUtils

/**
 * Intention to create go unit test
 */
class VacuumCreateGoUnitTestIntention : VacuumIntentionBaseAction(VacuumBundle.message("test.create")) {

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file !is GoFile || editor == null) return false
        if (GoTestFinder.isTestFile(file)) return false
        val target = getTargetFunctionOrMethod(editor, file) ?: return false
        return !VacuumPsiUtils.isInner(target)
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (editor == null || file == null || file !is GoFile) return
        val target = getTargetFunctionOrMethod(editor, file) ?: return
        val dialog = GoCreateTestDialog(
            project,
            file,
            target.name ?: return,
            VacuumBundle.message("test.create")
        )
        if (dialog.showAndGet()) return
    }
}
