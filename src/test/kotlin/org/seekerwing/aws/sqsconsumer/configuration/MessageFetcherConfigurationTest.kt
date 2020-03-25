package org.seekerwing.aws.sqsconsumer.configuration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MessageFetcherConfigurationTest {

    @Test
    fun getMaximumNumberOfMessages() {
        assertEquals(10, MessageFetcherConfiguration().maximumNumberOfMessages)
        assertEquals(42, MessageFetcherConfiguration(maximumNumberOfMessages = 42).maximumNumberOfMessages)
        assertEquals(24, MessageFetcherConfiguration(24, 42, 7).maximumNumberOfMessages)
    }

    @Test
    fun getWaitTimeSeconds() {
        assertEquals(20, MessageFetcherConfiguration().waitTimeSeconds)
        assertEquals(42, MessageFetcherConfiguration(waitTimeSeconds = 42).waitTimeSeconds)
        assertEquals(24, MessageFetcherConfiguration(7, 24, 42).waitTimeSeconds)
    }

    @Test
    fun getVisibilityTimeoutSeconds() {
        assertEquals(30, MessageFetcherConfiguration().visibilityTimeoutSeconds)
        assertEquals(42, MessageFetcherConfiguration(visibilityTimeoutSeconds = 42).visibilityTimeoutSeconds)
        assertEquals(24, MessageFetcherConfiguration(42, 7, 24).visibilityTimeoutSeconds)
    }
}
