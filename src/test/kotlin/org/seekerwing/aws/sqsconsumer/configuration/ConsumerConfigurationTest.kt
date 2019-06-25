package org.seekerwing.aws.sqsconsumer.configuration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ConsumerConfigurationTest {

    @Test
    fun getParallelism() {
        assertEquals(10, ConsumerConfiguration().parallelism)
        assertEquals(42, ConsumerConfiguration(42).parallelism)
    }
}