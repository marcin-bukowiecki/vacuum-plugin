/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.lint

/**
 * @author Marcin Bukowiecki
 */
interface LintProcess<T : LintResult<*>> {

    fun execute(): T

    fun command(): String
}
