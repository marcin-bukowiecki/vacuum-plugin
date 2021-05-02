/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix.helpers

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import io.vacuum.quickfix.RedundantBooleanLiteralQuickFix

/**
 * @author Marcin Bukowiecki
 */
interface RedundantBooleanLiteralStrategy {

    fun applyFix(project: Project, descriptor: ProblemDescriptor, caller: RedundantBooleanLiteralQuickFix)
}
