/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

import com.google.common.base.Objects

/**
 * @author Marcin Bukowiecki
 */
class RegisterKey(private val startLine: Int, private val endLine: Int) {

    override fun hashCode(): Int {
        return Objects.hashCode(startLine, endLine)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is RegisterKey) {
            return false
        }
        return this.endLine == other.startLine
    }
}
