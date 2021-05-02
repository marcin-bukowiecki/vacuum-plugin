/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.gotest

import com.goide.psi.GoFile
import com.goide.psi.impl.GoElementFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager

/**
 * @author Marcin Bukowiecki
 */
class NewUnitTestCreator(
    private val createdFile: VirtualFile,
    private val sourceFile: GoFile,
    private val unitTestFunctionName: String
) : UnitTestCreator {

    override fun applyTest() {
        val project = sourceFile.project
        val packageName = sourceFile.packageName ?: return
        val toAdd = GoElementFactory.createFileFromText(project, createText(packageName, unitTestFunctionName))
        val goFile = PsiManager.getInstance(project).findFile(createdFile) as? GoFile ?: return
        for (child in toAdd.children) {
            goFile.add(child)
        }
        goFile.functions.lastOrNull()?.let {
            fn ->
            focusOnCreatedFunction(project, fn, createdFile)
        }
    }
}
