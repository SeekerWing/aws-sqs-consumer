package org.seekerwing.aws.sqsconsumer.messageprovider

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.configuration.MessageFetcherConfiguration
import org.seekerwing.aws.sqsconsumer.configuration.MessageProviderConfiguration
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope
import org.seekerwing.aws.sqsconsumer.model.Queue
import org.seekerwing.aws.sqsconsumer.sqs.launchMessageFetcher

internal class MultipleQueueBasedMessageProviderTest {

    @Test
    @DisplayName("validate that provideMessages invokes launchMessageFetcher for each queue in MessageProviderConfiguration N (parallelism) times")
    fun provideMessage() = runBlockingTest {
        val fetcherConfiguration = MessageFetcherConfiguration()
        val queue1: Queue = mockk()
        val providerConfiguration1 = MessageProviderConfiguration(queue1, fetcherConfiguration, 2)
        val queue2: Queue = mockk()
        val providerConfiguration2 = MessageProviderConfiguration(queue2, fetcherConfiguration, 4)
        val queue3: Queue = mockk()
        val providerConfiguration3 = MessageProviderConfiguration(queue3, fetcherConfiguration, 8)
        val providerConfigurations = setOf(providerConfiguration1, providerConfiguration2, providerConfiguration3)

        mockkStatic("org.seekerwing.aws.sqsconsumer.sqs.MessageFetcherLauncherKt")

        coEvery {
            any<CoroutineScope>().launchMessageFetcher(eq(providerConfiguration1), any<SendChannel<MessageEnvelope>>())
        } returns Job()
        coEvery {
            any<CoroutineScope>().launchMessageFetcher(eq(providerConfiguration2), any<SendChannel<MessageEnvelope>>())
        } returns Job()
        coEvery {
            any<CoroutineScope>().launchMessageFetcher(eq(providerConfiguration3), any<SendChannel<MessageEnvelope>>())
        } returns Job()

        val multipleQueueBasedMessageProvider = MultipleQueueBasedMessageProvider(providerConfigurations)
        multipleQueueBasedMessageProvider.provideMessages(this)

        coVerify(exactly = 2) {
            any<CoroutineScope>().launchMessageFetcher(eq(providerConfiguration1), any<SendChannel<MessageEnvelope>>())
        }
        coVerify(exactly = 4) {
            any<CoroutineScope>().launchMessageFetcher(eq(providerConfiguration2), any<SendChannel<MessageEnvelope>>())
        }
        coVerify(exactly = 8) {
            any<CoroutineScope>().launchMessageFetcher(eq(providerConfiguration3), any<SendChannel<MessageEnvelope>>())
        }
    }
}
