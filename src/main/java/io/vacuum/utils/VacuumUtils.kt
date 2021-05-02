/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.utils

import com.goide.psi.GoFile
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.refactoring.suggested.startOffset
import io.vacuum.codesmells.CognitiveComplexity
import io.vacuum.codesmells.ComplexBooleanExpressionsInspection
import io.vacuum.codesmells.SwitchStatementInspection
import io.vacuum.configurable.LinesInspection
import io.vacuum.configurable.ParametersInspection
import io.vacuum.inspections.VacuumBaseLocalInspection

/**
 * @author Marcin Bukowiecki
 */
object VacuumUtils {
    
    fun asGoFile(project: Project, virtualFile: VirtualFile): GoFile? {
        return PsiManager.getInstance(project).findFile(virtualFile) as? GoFile
    }

    fun focusEditor(project: Project, virtualFile: VirtualFile, offset: Int) {
        FileEditorManager.getInstance(project)
            .openEditor(OpenFileDescriptor(project, virtualFile, offset), true)
    }

    fun focusEditor(project: Project, virtualFile: VirtualFile) {
        FileEditorManager.getInstance(project)
            .openEditor(OpenFileDescriptor(project, virtualFile), true)
    }

    fun focusEditor(project: Project, virtualFile: VirtualFile, function: GoFunctionOrMethodDeclaration) {
        FileEditorManager.getInstance(project)
            .openEditor(OpenFileDescriptor(project, virtualFile, function.lastChild.startOffset), true)
    }

    fun String.isBool(): Boolean {
        return this == "true" || this == "false"
    }

    fun String.isAnd(): Boolean {
        return this == "and"
    }

    fun String.isEq(): Boolean {
        return this == "=="
    }

    fun String.isNotEq(): Boolean {
        return this == "!="
    }

    fun String.isTrue(): Boolean {
        return this == "true"
    }

    fun String.isFalse(): Boolean {
        return this == "false"
    }

    fun rerunIntentions() {
        ProjectManager.getInstanceIfCreated()?.let { projectManager ->
            projectManager.openProjects.forEach { project ->
                val psiManager = PsiManager.getInstance(project)
                FileEditorManager.getInstance(project).allEditors.forEach {
                    if (it.file?.extension == "go") {
                        val goFile = psiManager.findFile(it.file ?: return) as? GoFile ?: return
                        DaemonCodeAnalyzer.getInstance(project).restart(goFile)
                    }
                }
            }
        }
    }

    private fun getConfigurableIntentions(): List<VacuumBaseLocalInspection> {
        return listOf(
            CognitiveComplexity(),
            ComplexBooleanExpressionsInspection(),
            SwitchStatementInspection(),
            LinesInspection(),
            ParametersInspection()
        )
    }
}
