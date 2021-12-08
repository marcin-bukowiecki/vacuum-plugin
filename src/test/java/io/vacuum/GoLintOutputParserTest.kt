package io.vacuum

import io.vacuum.lint.GoLintOutputParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
        Assertions.assertEquals("main.go:9:6:exported type Person should have comment or be unexported",
            actual.toString())
    }

    @Test
    fun testParseContent_2() {
        val output = "\n"
        val parser = GoLintOutputParser()
        val actual = parser.parseLine(output)
        Assertions.assertEquals(null, actual)
    }
}
