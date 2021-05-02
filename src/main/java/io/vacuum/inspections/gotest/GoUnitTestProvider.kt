/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.gotest

import com.goide.psi.GoFile
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.util.IncorrectOperationException
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumNamings

/**
 * @author Marcin Bukowiecki
 */
class GoUnitTestProvider(
    private val unitTestFunctionName: String,
    private val goFilePtr: SmartPsiElementPointer<GoFile>
) {

    fun applyTest() {
        applyTest(goFilePtr.element ?: return)
    }

    private fun applyTest(containingFile: GoFile) {
        val project = containingFile.project
        val sourceFileVF = containingFile.virtualFile
        val toCreate = sourceFileVF.nameWithoutExtension + VacuumNamings.testFileSuffix

        sourceFileVF.parent.findChild(toCreate).let { testFile ->
            WriteCommandAction
                .writeCommandAction(project)
                .withName(VacuumBundle.message("test.create.command"))
                .compute(object : ThrowableComputable<Unit, IncorrectOperationException> {

                        override fun compute() {
                            if (testFile == null) {
                                NewUnitTestCreator(
                                    sourceFileVF.parent
                                        .createChildData(this, toCreate), containingFile, unitTestFunctionName
                                ).applyTest()
                            } else {
                                ExistingUnitTestCreator(testFile, containingFile, unitTestFunctionName).applyTest()
                            }
                        }
                    }
                )
        }
    }
}
