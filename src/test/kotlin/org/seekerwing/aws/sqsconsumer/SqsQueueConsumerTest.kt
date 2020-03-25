package org.seekerwing.aws.sqsconsumer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.messageprovider.MessageProvider
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope

internal class SqsQueueConsumerTest {

    val receiveChannel: ReceiveChannel<MessageEnvelope> = mockk()
    val messageConsumer: MessageConsumer = mockk()
    val messageProvider: MessageProvider = mockk()

    @Test
    @DisplayName("validate that start invokes MessageProvider.provideMessages and MessageConsumer.launchConsumer")
    fun start() = runBlockingTest {
        val sqsQueueConsumer = SqsQueueConsumer(messageProvider, messageConsumer)

        coEvery { messageProvider.provideMessages(any<CoroutineScope>()) } returns receiveChannel
        coEvery { messageConsumer.launchConsumer(any<CoroutineScope>(), eq(receiveChannel)) } returns Unit

        sqsQueueConsumer.start()

        coVerify(exactly = 1) { messageProvider.provideMessages(any<CoroutineScope>()) }
        coVerify(exactly = 1) { messageConsumer.launchConsumer(any<CoroutineScope>(), eq(receiveChannel)) }
    }
}
