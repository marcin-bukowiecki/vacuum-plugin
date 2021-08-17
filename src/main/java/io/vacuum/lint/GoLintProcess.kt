/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.lint

import com.goide.sdk.GoSdkUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import io.vacuum.notifications.VacuumNotifier
import io.vacuum.settings.VacuumSettingsState
import io.vacuum.utils.VacuumBundle
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author Marcin Bukowiecki
 */
class GoLintProcess(private val files: List<PsiFile>, private val project: Project): LintProcess<GoLintResult> {

    constructor(file: PsiFile): this(listOf(file), file.project)

    override fun execute(): GoLintResult {
        if (!checkGoLint(this.project)) {
            return GoLintResult(emptyList())
        }

        val messages = mutableListOf<GoLintMessage>()

        for (f in files) {
            val process = executeLint(f.project, f.virtualFile.path) ?: return GoLintResult(emptyList())
            val br = BufferedReader(InputStreamReader(process.inputStream))

            while (true) {
                val line = br.readLine() ?: break
                GoLintOutputParser().parseLine(line)?.let { it -> messages.add(it) }
            }
        }

        return GoLintResult(messages)
    }

    override fun command(): String {
        return "golint"
    }

    companion object {

        private val log = Logger.getInstance(GoLintProcess::javaClass.name)

        fun executeLint(project: Project, filePath: String): Process? {
            val process = tryExecute(pathPrefix = "", filePath)
            if (process != null) return process
            val goPathBins = GoSdkUtil.getGoPathRoots(project, null)
            for (goPathBin in goPathBins) {
                val p = tryExecute(goPathBin.path + "/bin/", filePath)
                if (p != null) {
                    return p
                }
            }
            return null
        }

        fun checkGoLint(project: Project, showNotification: Boolean = false): Boolean {
            if (!VacuumSettingsState.getInstance().enableGoLint) {
                return false
            }
            if (executeLint(project, "-foo") == null) {
                if (showNotification) {
                    VacuumNotifier.notifyError(
                        project,
                        VacuumBundle.message("vacuum.golint.notification.title"),
                        VacuumBundle.message("vacuum.golint.notification.message")
                    )
                }
                return false
            }
            return true
        }

        private fun tryExecute(pathPrefix: String, filePath: String): Process? {
            return try {
                Runtime.getRuntime().exec(pathPrefix + "golint $filePath")
            } catch (e: Exception) {
                log.debug("Exception while checking golint", e)
                null
            }
        }
    }
}
