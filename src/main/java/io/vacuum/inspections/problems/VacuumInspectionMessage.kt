/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.inspections.problems

import com.goide.inspections.core.GoInspectionMessage

/**
 * @author Marcin Bukowiecki
 */
class VacuumInspectionMessage(private val message: String) : GoInspectionMessage {

    override fun getId(): String {
        return message
    }

    override fun toString(): String {
        return message
    }
}
