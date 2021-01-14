package org.seekerwing.aws.sqsconsumer.sqs

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.configuration.MessageFetcherConfiguration
import org.seekerwing.aws.sqsconsumer.configuration.MessageProviderConfiguration
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope
import org.seekerwing.aws.sqsconsumer.model.Queue
import software.amazon.awssdk.services.sqs.model.Message

internal class MessageFetcherLauncherTest : CoroutineScope {

    private val supervisorJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + supervisorJob

    val queue: Queue = mockk()
    val channel: SendChannel<MessageEnvelope> = mockk(relaxUnitFun = true)

    @Test
    @DisplayName("validate that launchMessageFetcher invokes Queue.fetchMessage and Channel.send each Message")
    fun launchMessageFetcher() {
        val fetcherConfiguration = MessageFetcherConfiguration()
        val providerConfiguration = MessageProviderConfiguration(queue, fetcherConfiguration)

        mockkStatic("org.seekerwing.aws.sqsconsumer.sqs.MessageFetcherKt")

        coEvery { queue.fetchMessage(fetcherConfiguration) } returns
                listOf(Message.builder().body("M1").build()) andThen
                listOf(Message.builder().body("M2").build()) andThenThrows
                RuntimeException("everything explodes 💣") andThen
                listOf(Message.builder().body("M3").build()) andThen
                listOf(Message.builder().body("M4").build()) andThen
                listOf()

        val messageFetcher = launchMessageFetcher(providerConfiguration, channel)
        runBlocking {
            delay(5000L)
            messageFetcher.cancelAndJoin()
        }

        coVerify { channel.send(MessageEnvelope(setOf(Message.builder().body("M1").build()), queue)) }
        coVerify { channel.send(MessageEnvelope(setOf(Message.builder().body("M2").build()), queue)) }
        coVerify { channel.send(MessageEnvelope(setOf(Message.builder().body("M3").build()), queue)) }
        coVerify { channel.send(MessageEnvelope(setOf(Message.builder().body("M4").build()), queue)) }
    }
}
