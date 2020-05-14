package org.seekerwing.aws.sqsconsumer.configuration

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.model.Queue

internal class MessageProviderConfigurationTest {

    private val fetcherConfiguration: MessageFetcherConfiguration = mockk()
    private val queue: Queue = mockk()

    @Test
    fun getQueue() {
        assertEquals(queue, MessageProviderConfiguration(queue, fetcherConfiguration).queue)
        assertEquals(queue, MessageProviderConfiguration(queue, fetcherConfiguration, PARALLELISM).queue)
    }

    @Test
    fun getMessageFetcherConfiguration() {
        assertEquals(fetcherConfiguration,
            MessageProviderConfiguration(queue, fetcherConfiguration).messageFetcherConfiguration)
        assertEquals(fetcherConfiguration,
            MessageProviderConfiguration(queue, fetcherConfiguration, PARALLELISM).messageFetcherConfiguration)
    }

    @Test
    fun getParallelism() {
        assertEquals(1, MessageProviderConfiguration(queue, fetcherConfiguration).parallelism)
        assertEquals(PARALLELISM, MessageProviderConfiguration(queue, fetcherConfiguration, PARALLELISM).parallelism)
    }

    @Test
    fun getDefaultConfiguration() {
        assertEquals(10, MessageProviderConfiguration(queue).messageFetcherConfiguration.maximumNumberOfMessages)
    }

    companion object {
        const val PARALLELISM = 42
    }
}
