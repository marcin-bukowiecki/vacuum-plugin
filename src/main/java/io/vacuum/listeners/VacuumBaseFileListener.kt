package io.vacuum.listeners

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener

/**
 * @author Marcin Bukowiecki
 */
abstract class VacuumBaseFileListener : BulkFileListener {

    protected fun getRootManager(project: Project, cache: MutableMap<Project, ProjectRootManager>): ProjectRootManager? {
        val value = cache[project]
        return if (value == null) {
            val created = ProjectRootManager.getInstance(project)
            cache[project] = created
            created
        } else {
            value
        }
    }
}