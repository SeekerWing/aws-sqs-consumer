package org.seekerwing.aws.sqsconsumer.configuration

import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.seekerwing.aws.sqsconsumer.model.Queue

internal class MessageProviderConfigurationTest {

    val queue: Queue = mockk()
    val messageFetcherConfiguration: MessageFetcherConfiguration = mockk()

    @Test
    fun getQueue() {
        assertEquals(queue, MessageProviderConfiguration(queue, messageFetcherConfiguration).queue)
        assertEquals(queue, MessageProviderConfiguration(queue, messageFetcherConfiguration, 42).queue)
    }

    @Test
    fun getMessageFetcherConfiguration() {
        assertEquals(messageFetcherConfiguration, MessageProviderConfiguration(queue, messageFetcherConfiguration).messageFetcherConfiguration)
        assertEquals(messageFetcherConfiguration, MessageProviderConfiguration(queue, messageFetcherConfiguration, 42).messageFetcherConfiguration)
    }

    @Test
    fun getParallelism() {
        assertEquals(1, MessageProviderConfiguration(queue, messageFetcherConfiguration).parallelism)
        assertEquals(42, MessageProviderConfiguration(queue, messageFetcherConfiguration, 42).parallelism)
    }
}