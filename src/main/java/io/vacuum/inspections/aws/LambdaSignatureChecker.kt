/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.aws

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoExpression
import com.goide.psi.GoFunctionDeclaration
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoSignature
import com.goide.psi.GoTypeList
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoTypeUtil
import com.intellij.psi.PsiElement
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumPsiUtils

/**
 * @author Marcin Bukowiecki
 */
class LambdaSignatureChecker(private val holder: GoProblemsHolder,
                             private val argument: GoExpression) : GoVisitor() {

    fun check(element: PsiElement) {
        element.accept(this)
    }

    fun check(signature: GoSignature) {
        checkSignature(argument, signature)
    }

    override fun visitMethodDeclaration(o: GoMethodDeclaration) {
        checkSignature(o, o.signature ?: return)
    }

    override fun visitFunctionDeclaration(o: GoFunctionDeclaration) {
        checkSignature(o, o.signature ?: return)
    }

    private fun checkSignature(o: PsiElement, signature: GoSignature) {
        val parameters = signature.parameters
        if (parameters.isVariadic) {
            holder.registerProblem(
                o,
                VacuumBundle.vacuumInspectionMessage("vacuum.aws.lambda.parameters.variadic")
            )
            return
        }

        val parameterDeclarationList = parameters.parameterDeclarationList
        if (parameterDeclarationList.size !in 0..2) {
            holder.registerProblem(
                o,
                VacuumBundle.vacuumInspectionMessage("vacuum.aws.lambda.parameters.number")
            )
            return
        }

        if (parameterDeclarationList.size == 2) {
            val goParameterDeclaration = parameterDeclarationList[0]
            val type = goParameterDeclaration.type ?: return

            VacuumPsiUtils.findImport(goParameterDeclaration, "context")?.let { foundImport ->
                val alias = foundImport.alias ?: "context"
                if (type.text != "$alias.Context") {
                    holder.registerProblem(
                        o,
                        VacuumBundle.vacuumInspectionMessage("vacuum.aws.lambda.signature.context")
                    )
                }
            }
        }

        val resultType = signature.resultType
        if (resultType is GoTypeList) {
            if (resultType.typeList.size !in 0..2) {
                holder.registerProblem(
                    o,
                    VacuumBundle.vacuumInspectionMessage("vacuum.aws.lambda.result.number")
                )
                return
            }

            resultType.typeList.lastOrNull()?.let { last ->
                if (!GoTypeUtil.isError(last, o)) {
                    holder.registerProblem(
                        o,
                        VacuumBundle.vacuumInspectionMessage("vacuum.aws.lambda.result.error")
                    )
                    return
                }
            }
        }
    }
}
