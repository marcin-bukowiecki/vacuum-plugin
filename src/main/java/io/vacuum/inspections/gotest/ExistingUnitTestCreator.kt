/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.gotest

import com.goide.psi.GoFile
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.goide.psi.impl.GoElementFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager

/**
 * @author Marcin Bukowiecki
 */
class ExistingUnitTestCreator(
    private val existingFile: VirtualFile,
    private val sourceFile: GoFile,
    private val unitTestFunctionName: String
) : UnitTestCreator {

    override fun applyTest() {
        val project = sourceFile.project
        PsiManager.getInstance(sourceFile.project).findFile(existingFile)?.let { psiFile ->
            val goFile = psiFile as? GoFile ?: return

            goFile.add(GoElementFactory.createNewLine(project, 2))

            var functionOrMethod = findFunctionOrMethod(goFile, unitTestFunctionName)

            if (functionOrMethod == null) {
                val packageName = sourceFile.packageName
                val created = GoElementFactory.createFileFromText(
                    project,
                    createText(packageName ?: return, unitTestFunctionName)
                )

                if (goFile.packageName == null) {
                    goFile.add(GoElementFactory.createPackageClause(project, packageName))
                    goFile.add(GoElementFactory.createNewLine(project))
                }

                functionOrMethod = created.functions.firstOrNull()
                functionOrMethod?.let {
                    val addedFunction = goFile.addAfter(it, goFile.lastChild) as GoFunctionOrMethodDeclaration
                    focusOnCreatedFunction(project, addedFunction, existingFile)
                }
            }
        }
    }

    private fun findFunctionOrMethod(goFile: GoFile, name: String): GoFunctionOrMethodDeclaration? {
        val foundFunction = goFile.functions.firstOrNull { fn -> fn.name != null && fn.name.equals(name) }
        return foundFunction ?: goFile.methods.firstOrNull { m -> m.name != null && m.name.equals(name) }
    }
}
