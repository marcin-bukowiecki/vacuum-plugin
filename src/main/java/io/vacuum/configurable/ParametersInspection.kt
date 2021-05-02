/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.configurable

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoParameters
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage

/**
 * @author Marcin Bukowiecki
 */
class ParametersInspection : VacuumBaseLocalInspection(), Configurable {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitParameters(parameters: GoParameters) {
                val size = parameters.definitionList.size
                val max = getMaxNumberOfParameters()
                if (max != -1 && size > max) {
                    holder.registerProblem(parameters, VacuumInspectionMessage("Too many parameters"))
                }
            }
        }
    }
}
