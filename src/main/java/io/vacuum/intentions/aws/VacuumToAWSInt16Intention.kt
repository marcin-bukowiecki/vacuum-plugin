/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.intentions.aws

import com.goide.psi.GoType
import com.goide.psi.impl.GoTypeUtil
import com.intellij.psi.PsiElement
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumTypeUtils

/**
 * @author Marcin Bukowiecki
 */
class VacuumToAWSInt16Intention : BaseToAWSIntention(VacuumBundle.message("vacuum.aws.int16.create")) {

  override val functionExpr: String
    get() = "aws.Int16(1)"

  override fun typeSupported(type: GoType, element: PsiElement): Boolean {
    return VacuumTypeUtils.isInt16(type, element)
  }
}
