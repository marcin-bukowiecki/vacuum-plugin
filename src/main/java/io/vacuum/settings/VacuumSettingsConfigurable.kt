/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.settings

import com.intellij.openapi.options.Configurable
import io.vacuum.utils.VacuumUtils
import javax.swing.JComponent

/**
 * @author Marcin Bukowiecki
 */
class VacuumSettingsConfigurable : Configurable {

    private var settingsComponent: VacuumSettingsComponent? = null

    override fun createComponent(): JComponent? {
        settingsComponent = VacuumSettingsComponent()
        return settingsComponent?.mainPanel
    }

    override fun isModified(): Boolean {
        val settings = VacuumSettingsState.getInstance()
        var modified: Boolean = settingsComponent?.functionLines?.text != settings?.functionLines ?: ""
        modified = modified || settingsComponent?.methodLines?.text != settings?.methodLines ?: ""
        modified = modified || settingsComponent?.sourceFileLines?.text != settings?.sourceFileLines ?: ""
        modified = modified || settingsComponent?.switchCaseLines?.text != settings?.switchCaseLines ?: ""
        modified = modified || settingsComponent?.functionParameters?.text != settings?.functionParameters ?: ""
        modified = modified || settingsComponent?.casesNumber?.text != settings?.casesNumber ?: ""
        modified = modified || settingsComponent?.cognitiveComplexity?.text != settings?.cognitiveComplexity ?: ""
        modified = modified || settingsComponent?.controlFlowDepth?.text != settings?.controlFlowDepth ?: ""
        modified = modified || settingsComponent?.booleanExpressions?.text != settings?.booleanExpressions ?: ""
        return modified
    }

    override fun apply() {
        val rerun = isModified

        val settings = VacuumSettingsState.getInstance()
        settings?.functionLines = settingsComponent?.functionLines?.text?.toInt() ?: return
        settings?.methodLines = settingsComponent?.methodLines?.text?.toInt() ?: return
        settings?.sourceFileLines = settingsComponent?.sourceFileLines?.text?.toInt() ?: return
        settings?.switchCaseLines = settingsComponent?.switchCaseLines?.text?.toInt() ?: return
        settings?.functionParameters = settingsComponent?.functionParameters?.text?.toInt() ?: return
        settings?.casesNumber = settingsComponent?.casesNumber?.text?.toInt() ?: return
        settings?.cognitiveComplexity = settingsComponent?.cognitiveComplexity?.text?.toInt() ?: return
        settings?.controlFlowDepth = settingsComponent?.controlFlowDepth?.text?.toInt() ?: return
        settings?.booleanExpressions = settingsComponent?.booleanExpressions?.text?.toInt() ?: return

        if (rerun) VacuumUtils.rerunIntentions()
    }

    override fun getDisplayName(): String {
        return "Vacuum Settings"
    }

    override fun reset() {
        val instance = VacuumSettingsState.getInstance()
        settingsComponent?.functionLines?.text = instance?.functionLines.toString()
        settingsComponent?.methodLines?.text = instance?.methodLines.toString()
        settingsComponent?.sourceFileLines?.text = instance?.sourceFileLines.toString()
        settingsComponent?.switchCaseLines?.text = instance?.switchCaseLines.toString()
        settingsComponent?.functionParameters?.text = instance?.functionParameters.toString()
        settingsComponent?.casesNumber?.text = instance?.casesNumber.toString()
        settingsComponent?.cognitiveComplexity?.text = instance?.cognitiveComplexity.toString()
        settingsComponent?.controlFlowDepth?.text = instance?.controlFlowDepth.toString()
        settingsComponent?.booleanExpressions?.text = instance?.booleanExpressions.toString()
    }
}
