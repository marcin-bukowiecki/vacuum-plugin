/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.listeners

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import io.vacuum.lint.GoLintProcess

/**
 * @author Marcin Bukowiecki
 */
class VacuumStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        GoLintProcess.checkGoLint(project, true)
    }
}
