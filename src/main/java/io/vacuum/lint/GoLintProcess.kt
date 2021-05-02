/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.lint

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import io.vacuum.notifications.VacuumNotifier
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author Marcin Bukowiecki
 */
class GoLintProcess(private val files: List<PsiFile>, private val project: Project): LintProcess<GoLintResult> {

    private val command = "golint"

    constructor(file: PsiFile): this(listOf(file), file.project)

    override fun execute(): GoLintResult {
        if (!checkGoLint(this.project)) {
            return GoLintResult(emptyList())
        }

        val messages = mutableListOf<GoLintMessage>()

        for (f in files) {
            val process = Runtime.getRuntime().exec("$command ${f.virtualFile.path}")
            val br = BufferedReader(InputStreamReader(process.inputStream))

            while (true) {
                val line = br.readLine() ?: break
                GoLintOutputParser().parseLine(line)?.let { it -> messages.add(it) }
            }
        }

        return GoLintResult(messages)
    }

    override fun command(): String {
        return command
    }

    companion object {

        private val log = Logger.getInstance(GoLintProcess::javaClass.name)

        fun checkGoLint(project: Project, showNotification: Boolean = false): Boolean {
            return try {
                Runtime.getRuntime().exec("golint -foo")
                true
            } catch (e: IOException) {
                if (showNotification) {
                    log.error("Exception while checking golint", e)
                    VacuumNotifier.notifyError(
                        project,
                        "golint is not installed",
                        "Please run <code>go get -u golang.org/x/lint/golint</code> to install it"
                    )
                }
                false
            }
        }
    }
}
