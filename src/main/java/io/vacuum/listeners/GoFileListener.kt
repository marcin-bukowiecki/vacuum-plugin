/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.listeners

import com.goide.psi.GoFile
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.psi.PsiManager
import io.vacuum.utils.VacuumUtils

/**
 * Listener to listen on create/move of go files
 *
 * @author Marcin Bukowiecki
 */
@Deprecated("Changed logic for saved files")
class GoFileListener : VacuumBaseFileListener() {

    override fun after(events: MutableList<out VFileEvent>) {
        val filtered = filterEvents(events)
        if (filtered.isEmpty()) return

        val projectRootManagerCache = mutableMapOf<Project, ProjectRootManager>()

        ProjectManager.getInstanceIfCreated()?.let { projectManager ->
            projectManager.openProjects.forEach { project ->
                val fileIndex = getRootManager(project, projectRootManagerCache)?.fileIndex
                if (fileIndex != null) {
                    for (evt in filtered) {
                        val file = evt.file
                        if (file != null && fileIndex.isInContent(file)) {
                            PsiManager.getInstance(project).findViewProvider(file)?.let { viewProvider ->
                                DaemonCodeAnalyzer.getInstance(project).restart(GoFile(viewProvider))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun filterEvents(events: MutableList<out VFileEvent>): List<VFileEvent> {
        return events.filter { evt -> evt.file != null &&
                (evt is VFileMoveEvent || evt is VFileCreateEvent) &&
                evt.file?.extension == VacuumUtils.goExtension }
    }
}
