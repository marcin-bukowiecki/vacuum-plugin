/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.utils

import com.intellij.openapi.util.Key

/**
 * @author Marcin Bukowiecki
 */
class VacuumMarkKey : Key<Boolean>("VacuumMarkKey") {

    companion object {
        val instance = VacuumMarkKey()
    }
}
