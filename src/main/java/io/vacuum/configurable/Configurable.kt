/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.configurable

import io.vacuum.settings.VacuumSettingsState

/**
 * @author Marcin Bukowiecki
 */
interface Configurable {

    fun getMaxSourceLines(): Int {
        return VacuumSettingsState.getInstance()?.sourceFileLines ?: return -1
    }

    fun getMaxFunctionLines(): Int {
        return VacuumSettingsState.getInstance()?.functionLines ?: return -1
    }

    fun getMaxMethodLines(): Int {
        return VacuumSettingsState.getInstance()?.methodLines ?: return -1
    }

    fun getMaxSwitchCaseLines(): Int {
        return VacuumSettingsState.getInstance()?.switchCaseLines ?: return -1
    }

    fun getMaxNumberOfParameters(): Int {
        return VacuumSettingsState.getInstance()?.functionParameters ?: return -1
    }

    fun getMaxNumberOfCases(): Int {
        return VacuumSettingsState.getInstance()?.casesNumber ?: return -1
    }

    fun getCognitiveComplexityLimit(): Int {
        return VacuumSettingsState.getInstance()?.cognitiveComplexity ?: return -1
    }

    fun getMaxNumberOfBooleanExpressions(): Int {
        return VacuumSettingsState.getInstance()?.booleanExpressions ?: return -1
    }

    fun getMaxControlFlowDepth(): Int {
        return VacuumSettingsState.getInstance()?.controlFlowDepth ?: return -1
    }

    fun Int.isValid(): Boolean {
        return this > -1
    }
}
