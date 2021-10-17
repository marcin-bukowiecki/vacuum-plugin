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

    fun getNumberOfStringForEfficientConcatenation(): Int {
        return VacuumSettingsState.getInstance().numberOfStringsForEfficientConcatenation
    }

    fun getMaxSourceLines(): Int {
        return VacuumSettingsState.getInstance().sourceFileLines
    }

    fun getMaxFunctionLines(): Int {
        return VacuumSettingsState.getInstance().functionLines
    }

    fun getMaxMethodLines(): Int {
        return VacuumSettingsState.getInstance().methodLines
    }

    fun getMaxSwitchCaseLines(): Int {
        return VacuumSettingsState.getInstance().switchCaseLines
    }

    fun getMaxNumberOfParameters(): Int {
        return VacuumSettingsState.getInstance().functionParameters
    }

    fun getMaxNumberOfCases(): Int {
        return VacuumSettingsState.getInstance().casesNumber
    }

    fun getCognitiveComplexityLimit(): Int {
        return VacuumSettingsState.getInstance().cognitiveComplexity
    }

    fun getMaxNumberOfBooleanExpressions(): Int {
        return VacuumSettingsState.getInstance().booleanExpressions
    }

    fun getMaxControlFlowDepth(): Int {
        return VacuumSettingsState.getInstance().controlFlowDepth
    }

    fun Int.isValid(): Boolean {
        return this > -1
    }
}
