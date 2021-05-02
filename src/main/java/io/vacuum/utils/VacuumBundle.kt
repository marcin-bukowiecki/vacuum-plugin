/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.utils

import com.intellij.DynamicBundle
import io.vacuum.inspections.problems.VacuumInspectionMessage
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey

const val BUNDLE = "messages.vacuum"

/**
 * @author Marcin Bukowiecki
 */
object VacuumBundle : DynamicBundle(BUNDLE) {

    fun message(key: @PropertyKey(resourceBundle = BUNDLE) String): @Nls String {
        return getMessage(key)
    }

    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, arg1: Any): @Nls String {
        return getMessage(key, arg1)
    }

    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, arg1: Any, arg2: Any): @Nls String {
        return getMessage(key, arg1, arg2)
    }

    fun vacuumInspectionMessage(@PropertyKey(resourceBundle = BUNDLE) key: String): VacuumInspectionMessage {
        return VacuumInspectionMessage(getMessage(key))
    }
}
