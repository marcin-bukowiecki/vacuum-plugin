/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.quickfix

import com.intellij.codeInspection.LocalQuickFix

/**
 * @author Marcin Bukowiecki
 */
abstract class VacuumBaseLocalQuickFix(private val text: String) : LocalQuickFix {

    override fun getFamilyName(): String {
        return text
    }
}
