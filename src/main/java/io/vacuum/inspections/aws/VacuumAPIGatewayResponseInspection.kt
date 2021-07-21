/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.aws

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoCompositeLit
import com.goide.psi.GoVisitor
import com.goide.psi.impl.GoTypeSpecImpl
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.SmartPointerManager
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.quickfix.aws.APIGatewayResponseStatusCodeQuickFix
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumAPIGatewayResponseInspection : VacuumBaseLocalInspection() {

    private val responseNames = setOf(
        "APIGatewayProxyResponse",
        "APIGatewayV2HTTPResponse",
    )

    override fun buildGoVisitor(
        holder: GoProblemsHolder,
        session: LocalInspectionToolSession
    ): GoVisitor {

        return object : GoVisitor() {

            override fun visitCompositeLit(compositeLit: GoCompositeLit) {
                (compositeLit.typeReferenceExpression?.resolve() as? GoTypeSpecImpl)?.let { type ->
                    if (!responseNames.contains(type.identifier.text)) {
                        return
                    }

                    compositeLit.literalValue?.let { literalValue ->
                        for (goElement in literalValue.elementList) {
                            if (goElement.key?.fieldName?.identifier?.text == "StatusCode") {
                                return
                            }
                        }
                    }

                    holder.registerProblem(
                        compositeLit,
                        VacuumBundle.vacuumInspectionMessage("vacuum.aws.APIGateway.statusCode"),
                        APIGatewayResponseStatusCodeQuickFix(
                            SmartPointerManager.createPointer(
                                compositeLit.literalValue ?: return
                            )
                        )
                    )
                }
            }
        }
    }
}