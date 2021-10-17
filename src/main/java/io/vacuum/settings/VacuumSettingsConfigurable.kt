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
        var modified: Boolean = settingsComponent?.functionLines?.text != settings.functionLines.toString()
        modified = modified || settingsComponent?.methodLines?.text != settings.methodLines.toString()
        modified = modified || settingsComponent?.sourceFileLines?.text != settings.sourceFileLines.toString()
        modified = modified || settingsComponent?.switchCaseLines?.text != settings.switchCaseLines.toString()
        modified = modified || settingsComponent?.functionParameters?.text != settings.functionParameters.toString()
        modified = modified || settingsComponent?.casesNumber?.text != settings.casesNumber.toString()
        modified = modified || settingsComponent?.cognitiveComplexity?.text != settings.cognitiveComplexity.toString()
        modified = modified || settingsComponent?.controlFlowDepth?.text != settings.controlFlowDepth.toString()
        modified = modified || settingsComponent?.booleanExpressions?.text != settings.booleanExpressions.toString()
        modified = modified || settingsComponent?.stringConcatenation?.text != settings.numberOfStringsForEfficientConcatenation.toString()
        modified = modified || settingsComponent?.enableGoLint?.isSelected != settings.enableGoLint
        return modified
    }

    override fun apply() {
        val rerun = isModified

        val settings = VacuumSettingsState.getInstance()

        settingsComponent?.functionLines?.text?.toInt()?.let {
            settings.functionLines = it
        }

        settingsComponent?.methodLines?.text?.toInt()?.let {
            settings.methodLines = it
        }

        settingsComponent?.sourceFileLines?.text?.toInt()?.let {
            settings.sourceFileLines = it
        }

        settingsComponent?.switchCaseLines?.text?.toInt()?.let {
            settings.switchCaseLines = it
        }

        settingsComponent?.functionParameters?.text?.toInt()?.let {
            settings.functionParameters = it
        }

        settingsComponent?.casesNumber?.text?.toInt()?.let {
            settings.casesNumber = it
        }

        settingsComponent?.cognitiveComplexity?.text?.toInt()?.let {
            settings.cognitiveComplexity = it
        }

        settingsComponent?.controlFlowDepth?.text?.toInt()?.let {
            settings.controlFlowDepth = it
        }

        settingsComponent?.booleanExpressions?.text?.toInt()?.let {
            settings.booleanExpressions = it
        }

        settingsComponent?.stringConcatenation?.text?.toInt()?.let {
            settings.numberOfStringsForEfficientConcatenation = it
        }

        settingsComponent?.enableGoLint?.isSelected?.let {
            settings.enableGoLint = it
        }

        if (rerun) VacuumUtils.rerunIntentions()
    }

    override fun getDisplayName(): String {
        return "Vacuum Settings"
    }

    override fun reset() {
        val instance = VacuumSettingsState.getInstance()
        settingsComponent?.functionLines?.text = instance.functionLines.toString()
        settingsComponent?.methodLines?.text = instance.methodLines.toString()
        settingsComponent?.sourceFileLines?.text = instance.sourceFileLines.toString()
        settingsComponent?.switchCaseLines?.text = instance.switchCaseLines.toString()
        settingsComponent?.functionParameters?.text = instance.functionParameters.toString()
        settingsComponent?.casesNumber?.text = instance.casesNumber.toString()
        settingsComponent?.cognitiveComplexity?.text = instance.cognitiveComplexity.toString()
        settingsComponent?.controlFlowDepth?.text = instance.controlFlowDepth.toString()
        settingsComponent?.booleanExpressions?.text = instance.booleanExpressions.toString()
        settingsComponent?.stringConcatenation?.text = instance.numberOfStringsForEfficientConcatenation.toString()
        settingsComponent?.enableGoLint?.isSelected = instance.enableGoLint
    }
}
