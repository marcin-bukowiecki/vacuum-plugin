/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.receiver

import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.utils.VacuumBundle

/**
 * @author Marcin Bukowiecki
 */
class VacuumGenericReceiverNameInspection : VacuumBaseLocalInspection() {

    private val names = setOf(
        "me",
        "this",
        "self"
    )

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            override fun visitMethodDeclaration(o: GoMethodDeclaration) {
                o.receiver?.let { goReceiver ->
                    goReceiver.name?.let { name ->
                        val nameIdentifier = goReceiver.nameIdentifier ?: return
                        if (names.contains(name)) {
                            holder.registerProblem(
                                nameIdentifier,
                                VacuumBundle.vacuumInspectionMessage("vacuum.receiver.genericNames")
                            )
                        }
                    }
                }
            }
        }
    }
}