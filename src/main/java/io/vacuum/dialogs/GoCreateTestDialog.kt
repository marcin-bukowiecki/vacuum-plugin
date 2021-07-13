/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.dialogs

import com.goide.psi.GoFile
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiManager
import com.intellij.psi.SmartPointerManager
import com.intellij.ui.EditorTextField
import com.intellij.util.ui.JBUI
import io.vacuum.inspections.gotest.GoUnitTestProvider
import io.vacuum.utils.VacuumBundle
import io.vacuum.utils.VacuumNamings
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * @author Marcin Bukowiecki
 */
class GoCreateTestDialog(private val project: Project,
                         private val goFile: GoFile,
                         private val functionOrMethodName: String,
                         title: String) : DialogWrapper(project, true) {

    private lateinit var myTargetTestName: EditorTextField
    private lateinit var myUnitTestExistsPanel: JPanel
    private lateinit var myUnitTestExistsLabel: JLabel

    init {
        setTitle(title)
        init()
    }

    override fun createCenterPanel(): JComponent {
        myTargetTestName = EditorTextField(VacuumNamings.getUnitTestName(functionOrMethodName))

        val panel = JPanel(GridBagLayout())

        val constr = GridBagConstraints()
        constr.fill = GridBagConstraints.HORIZONTAL
        constr.anchor = GridBagConstraints.WEST

        var gridy = 1

        constr.insets = insets(4)
        constr.gridy = gridy++
        constr.gridx = 0
        constr.weightx = 0.0
        val libLabel = JLabel(VacuumBundle.message("vacuum.test.name"))
        libLabel.labelFor = myTargetTestName
        panel.add(libLabel, constr)

        constr.gridx = 1
        constr.weightx = 1.0
        constr.gridwidth = GridBagConstraints.REMAINDER
        panel.add(myTargetTestName, constr)

        myUnitTestExistsPanel = JPanel(BorderLayout())
        myUnitTestExistsLabel = JLabel()
        myUnitTestExistsLabel.icon = AllIcons.Actions.IntentionBulb
        myUnitTestExistsLabel.text = VacuumBundle.message("vacuum.test.error.exists")
        myUnitTestExistsPanel.add(myUnitTestExistsLabel, BorderLayout.CENTER)
        validateText()

        constr.insets = insets(1)
        constr.gridy = gridy++
        constr.gridx = 0
        panel.add(myUnitTestExistsPanel, constr)

        constr.gridheight = 1

        myTargetTestName.document.addDocumentListener(object : DocumentListener {

            override fun documentChanged(event: DocumentEvent) {
                validateText()
            }
        })

        return panel
    }

    override fun doOKAction() {
        GoUnitTestProvider(myTargetTestName.text, SmartPointerManager.createPointer(goFile)).applyTest()
        super.doOKAction()
    }

    private fun insets(top: Int): Insets {
        return insets(top, 0)
    }

    private fun insets(top: Int, bottom: Int): Insets {
        return JBUI.insets(top, 8, bottom, 8)
    }

    private fun validateText() {
        val content = myTargetTestName.text
        val sourceFile = goFile.virtualFile
        sourceFile.parent.findChild(sourceFile.nameWithoutExtension + VacuumNamings.testFileSuffix)?.let {
            val goFile = PsiManager.getInstance(project).findFile(it) as? GoFile ?: return
            goFile.functions.firstOrNull { fn -> fn.name == content }?.let {
                disableOk()
            } ?: enableOk()
        } ?: enableOk()
    }

    private fun enableOk() {
        myUnitTestExistsPanel.isVisible = false
        okAction.isEnabled = true
    }

    private fun disableOk() {
        myUnitTestExistsPanel.isVisible = true
        okAction.isEnabled = false
    }
}