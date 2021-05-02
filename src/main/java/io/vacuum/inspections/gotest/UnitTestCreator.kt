/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.gotest

import com.goide.psi.GoFunctionOrMethodDeclaration
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.refactoring.suggested.endOffset
import io.vacuum.utils.VacuumUtils

/**
 * @author Marcin Bukowiecki
 */
interface UnitTestCreator {

    fun applyTest()

    fun createText(packageName: String, unitTestName: String): String {
        var acc = ""
        acc += "package $packageName\n\n"
        acc += "import \"testing\"\n\n"
        acc += "func $unitTestName(t *testing.T) {}"
        return acc
    }

    fun focusOnCreatedFunction(project: Project,
                               addedFunction: GoFunctionOrMethodDeclaration,
                               existingFile: VirtualFile) {

        val children = addedFunction.block?.children ?: return
        if (children.isEmpty()) {
            return
        }
        val leftCurl = children[0] ?: return
        VacuumUtils.focusEditor(project, existingFile, leftCurl.endOffset)
    }
}
