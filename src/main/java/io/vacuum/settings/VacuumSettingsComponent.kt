/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

/**
 * @author Marcin Bukowiecki
 */
class VacuumSettingsComponent {

    val mainPanel: JPanel
    val enableGoLint = JBCheckBox()
    val sourceFileLines = JBTextField()
    val switchCaseLines = JBTextField()
    val functionLines = JBTextField()
    val methodLines = JBTextField()
    val functionParameters = JBTextField()
    val casesNumber = JBTextField()
    var cognitiveComplexity = JBTextField()
    var booleanExpressions = JBTextField()
    var controlFlowDepth = JBTextField()
    var stringConcatenation = JBTextField()

    init {
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(
                JBLabel("Enable golint"), enableGoLint,
                1, false
            )
            .addLabeledComponent(
                JBLabel("Source file lines threshold"), sourceFileLines,
                1, false
            )
            .addLabeledComponent(
                JBLabel("Switch case lines threshold"), switchCaseLines,
                1, false
            )
            .addLabeledComponent(JBLabel("Function lines threshold"), functionLines, 1, false)
            .addLabeledComponent(JBLabel("Method lines threshold"), methodLines, 1, false)
            .addLabeledComponent(
                JBLabel("Function number of parameters threshold"), functionParameters,
                1, false
            )
            .addLabeledComponent(
                JBLabel("Number of case branches threshold"), casesNumber,
                1, false
            )
            .addLabeledComponent(JBLabel("Cognitive complexity limit"), cognitiveComplexity, 1, false)
            .addLabeledComponent(
                JBLabel("Number of boolean expressions"), booleanExpressions,
                1, false
            )
            .addLabeledComponent(
                JBLabel("Control flow depth"), controlFlowDepth,
                1, false
            )
            .addLabeledComponent(
                JBLabel("Number of strings for concatenation optimization"), stringConcatenation,
                1, false
            )
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
