/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.aws

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoCallExpr
import com.goide.psi.GoFile
import com.goide.psi.GoFunctionType
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoTypeUtil
import com.intellij.codeInspection.LocalInspectionToolSession
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumLambdaFunctionSignatureInspection : VacuumBaseLocalInspection() {

    private val importPath = "github.com/aws/aws-lambda-go/lambda"

    override fun buildGoVisitor(holder: GoProblemsHolder, p1: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitCallExpr(o: GoCallExpr) {
                val expression = o.expression
                if (expression.reference?.canonicalText == "Start") {
                    val p = getAlias(o)
                    if (p.isEmpty()) return

                    val goExpression = o.argumentList.expressionList[0]
                    GoTypeUtil.getTypesOfExpressions(mutableListOf(goExpression))[0]?.let {
                        if (it !is GoFunctionType) {
                            holder.registerProblem(
                                goExpression,
                                VacuumBundle.vacuumInspectionMessage("vacuum.aws.lambda.signature.functionPtr")
                            )
                        } else {
                            it.signature?.let { sig ->
                                LambdaSignatureChecker(holder, goExpression).check(sig)
                            }
                        }
                    }

                    goExpression.reference?.resolve()?.let { resolvedReference ->
                        LambdaSignatureChecker(holder, goExpression).check(resolvedReference)
                    }
                }
            }
        }
    }

    private fun getAlias(o: GoCallExpr): String {
        val containingFile = o.containingFile as? GoFile ?: return ""
        val foundImport = containingFile.imports.firstOrNull { it.path == importPath } ?: return ""

        return foundImport.alias ?: "lambda"
    }
}