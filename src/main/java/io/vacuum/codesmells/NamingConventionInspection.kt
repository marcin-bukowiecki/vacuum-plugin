/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoFunctionDeclaration
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoParameters
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.PsiElement
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.utils.VacuumBundle
import java.util.regex.Pattern

/**
 * @author Marcin Bukowiecki
 */
class NamingConventionInspection : VacuumBaseLocalInspection() {

    private val regex = Pattern.compile("([a-zA-Z0-9]+)+")

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitFunctionDeclaration(functionDeclaration: GoFunctionDeclaration) {
                checkName(
                    functionDeclaration.nameIdentifier ?: return,
                    functionDeclaration.name.toString(), holder
                )
            }

            override fun visitMethodDeclaration(methodDeclaration: GoMethodDeclaration) {
                checkName(
                    methodDeclaration.nameIdentifier ?: return,
                    methodDeclaration.name.toString(), holder
                )
            }

            override fun visitParameters(parameters: GoParameters) {
                parameters.definitionList.forEach { param ->
                    if (param.name != null && param.name == "_") {
                        return
                    }
                    checkName(param.nameIdentifier ?: return, param.name.toString(), holder)
                }
            }
        }
    }

    private fun checkName(element: PsiElement, name: String, holder: GoProblemsHolder) {
        if (!regex.matcher(name).matches()) {
            holder.registerProblem(
                element,
                VacuumBundle.vacuumInspectionMessage("name.convention")
            )
        }
    }
}


