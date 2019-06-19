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

internal class SingleQueueBasedMessageProviderTest {

    @Test
    @DisplayName("validate that provideMessage invokes launchMessageFetcher N (parallelism) times")
    fun provideMessage() = runBlockingTest {
        val queue: Queue = mockk()
        val fetcherConfiguration = MessageFetcherConfiguration()
        val providerConfiguration = MessageProviderConfiguration(queue, fetcherConfiguration, 2)

        mockkStatic("org.seekerwing.aws.sqsconsumer.sqs.MessageFetcherLauncherKt")

        coEvery {
            any<CoroutineScope>().launchMessageFetcher(eq(providerConfiguration), any<SendChannel<MessageEnvelope>>())
        } returns Job()

        val singleQueueBasedMessageProvider = SingleQueueBasedMessageProvider(providerConfiguration)
        singleQueueBasedMessageProvider.provideMessage(this)

        coVerify(exactly = 2) {
            any<CoroutineScope>().launchMessageFetcher(eq(providerConfiguration), any<SendChannel<MessageEnvelope>>())
        }
    }
}