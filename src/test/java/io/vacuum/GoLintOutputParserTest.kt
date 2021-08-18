package io.vacuum

import io.vacuum.lint.GoLintOutputParser
import org.junit.Assert
import org.junit.Test

/**
 * @author Marcin Bukowiecki
 */
class GoLintOutputParserTest {

    @Test
    fun testParseContent_1() {
        val output = "main.go:9:6: exported type Person should have comment or be unexported\n" +
                "\n"
        val parser = GoLintOutputParser()
        val actual = parser.parseLine(output)
        Assert.assertEquals("main.go:9:6:exported type Person should have comment or be unexported",
            actual.toString())
    }

    @Test
    fun testParseContent_2() {
        val output = "\n"
        val parser = GoLintOutputParser()
        val actual = parser.parseLine(output)
        Assert.assertEquals(null, actual)
    }
}
