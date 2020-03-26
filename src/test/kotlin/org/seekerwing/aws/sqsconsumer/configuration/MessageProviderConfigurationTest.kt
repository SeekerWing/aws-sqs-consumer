package org.seekerwing.aws.sqsconsumer.configuration

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.model.Queue

internal class MessageProviderConfigurationTest {

    private val messageFetcherConfiguration: MessageFetcherConfiguration = mockk()
    private val parallelism = 42
    private val queue: Queue = mockk()

    @Test
    fun getQueue() {
        assertEquals(queue, MessageProviderConfiguration(queue, messageFetcherConfiguration).queue)
        assertEquals(queue, MessageProviderConfiguration(queue, messageFetcherConfiguration, parallelism).queue)
    }

    @Test
    fun getMessageFetcherConfiguration() {
        assertEquals(messageFetcherConfiguration,
            MessageProviderConfiguration(queue, messageFetcherConfiguration).messageFetcherConfiguration)
        assertEquals(messageFetcherConfiguration,
            MessageProviderConfiguration(queue, messageFetcherConfiguration, parallelism).messageFetcherConfiguration)
    }

    @Test
    fun getParallelism() {
        assertEquals(1, MessageProviderConfiguration(queue, messageFetcherConfiguration).parallelism)
        assertEquals(42, MessageProviderConfiguration(queue, messageFetcherConfiguration, parallelism).parallelism)
    }
}
