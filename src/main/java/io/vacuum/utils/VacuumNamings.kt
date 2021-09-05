/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.utils

import com.goide.psi.GoFile

/**
 * @author Marcin Bukowiecki
 */
object VacuumNamings {

    private const val testFileNameSuffix = "_test"

    private const val testFunctionNamePrefix = "Test_"

    const val testFileSuffix = "_test.go"

    /**
     * Function returns possible supported unit test names
     */
    fun getPossibleUnitTestNames(functionName: String): Set<String> {
        return setOf(
            "Test_$functionName",
            "Test$functionName",
            "Test_handler_$functionName",
            "Test_Handler_$functionName",
            "TestHandler$functionName",
        )
    }

    /**
     * Creates Go unit test name
     */
    fun getUnitTestName(functionName: String): String {
        return testFunctionNamePrefix + functionName
    }

    /**
     * Creates Go unit test file name
     */
    fun getUnitTestFileName(file: GoFile): String {
        return file.virtualFile.nameWithoutExtension + testFileNameSuffix
    }

    fun getTestFileName(file: GoFile): String {
        val name = file.virtualFile.nameWithoutExtension
        return if (name.endsWith(testFileSuffix)) {
            name
        } else {
            name + testFileSuffix
        }
    }
}