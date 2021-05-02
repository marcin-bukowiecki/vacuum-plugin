package io.vacuum.utils

import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Marcin Bukowiecki
 */
class VacuumNamingsTest {

    @Test
    fun sanity() {
        Assertions.assertThat(VacuumNamings.getPossibleUnitTestNames("Foo"))
            .isEqualTo(setOf("TestFoo", "Test_Foo", "Test_handler_Foo", "Test_Handler_Foo", "TestHandlerFoo"))
    }
}