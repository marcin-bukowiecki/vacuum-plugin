/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.*
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.SmartPointerManager
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage
import io.vacuum.quickfix.EmptyCodeBlockQuickFix
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumMarkKey
import io.vacuum.utils.VacuumPsiUtils
import io.vacuum.utils.VacuumPsiUtils.toSmartPointer

/**
 * @author Marcin Bukowiecki
 */
class EmptyCodeBlockInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(
        holder: GoProblemsHolder,
        session: LocalInspectionToolSession
    ): GoVisitor {

        return object : GoVisitor() {

            override fun visitIfStatement(ifStatement: GoIfStatement) {
                ifStatement.block?.let { ifBlock ->
                    if (VacuumPsiUtils.isEmpty(ifBlock)) {
                        markWithProblem(ifBlock)
                        holder.registerProblem(ifStatement.`if`,
                            VacuumInspectionMessage("If has empty code block"),
                            EmptyCodeBlockQuickFix(ifBlock.toSmartPointer()))
                    }
                }
            }

            override fun visitElseStatement(elseStatement: GoElseStatement) {
                elseStatement.block?.let { ifBlock ->
                    if (VacuumPsiUtils.isEmpty(ifBlock)) {
                        markWithProblem(ifBlock)
                        holder.registerProblem(elseStatement.`else`,
                            VacuumInspectionMessage("Else has empty code block"),
                            EmptyCodeBlockQuickFix(ifBlock.toSmartPointer()))
                    }
                }
            }

            override fun visitFunctionDeclaration(fd: GoFunctionDeclaration) {
                if (fd.name == "init" || caretInBlock(fd.block)) {
                    return
                }

                if (VacuumPsiUtils.isEmpty(fd)) {
                    val elementToMark = if (fd.nameIdentifier == null) {
                        fd
                    } else {
                        fd.nameIdentifier!!
                    }

                    holder.registerProblem(elementToMark,
                        VacuumInspectionMessage(VacuumBundle.message("function.emptyBlock")),
                        EmptyCodeBlockQuickFix(SmartPointerManager.createPointer(fd.blockIfExists ?: return))
                    )
                }
            }
        }
    }

    private fun markWithProblem(block: GoBlock) {
        block.putUserData(VacuumMarkKey.instance, true)
    }
}
