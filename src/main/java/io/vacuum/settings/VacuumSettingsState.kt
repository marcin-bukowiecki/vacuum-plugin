/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * @author Marcin Bukowiecki
 */
@State(
    name = "io.vacuum.settings.VacuumSettingsState",
    storages = [Storage("VacuumSettingsPlugin.xml")]
)
class VacuumSettingsState : PersistentStateComponent<VacuumSettingsState> {

    val numberOfStringsForEfficientConcatenation = 5
    var enableGoLint = true
    var sourceFileLines = 500
    var switchCaseLines = 6
    var functionLines = 25
    var methodLines = 25
    var functionParameters = 5
    var casesNumber = 10
    var cognitiveComplexity = 15
    var booleanExpressions = 4
    var controlFlowDepth = 4

    override fun getState(): VacuumSettingsState {
        return this
    }

    override fun loadState(state: VacuumSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        @JvmStatic
        fun getInstance(): VacuumSettingsState {
            return ApplicationManager.getApplication().getService(VacuumSettingsState::class.java)
        }
    }
}
