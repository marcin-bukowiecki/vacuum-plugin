/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.concurrency

import com.goide.psi.GoSimpleStatement
import com.goide.psi.impl.GoElementFactory
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer

/**
 * @author Marcin Bukowiecki
 */
class EmptyCriticalSectionQuickfix(private val unlockStmtRef: SmartPsiElementPointer<GoSimpleStatement>) : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Add defer statement"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        unlockStmtRef.element?.let { element ->
            val createdDeferStatement = GoElementFactory.createDeferStatement(project, element.text)
            element.replace(createdDeferStatement)
        }
    }
}