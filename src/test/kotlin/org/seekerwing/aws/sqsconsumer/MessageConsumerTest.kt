package org.seekerwing.aws.sqsconsumer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.seekerwing.aws.sqsconsumer.configuration.ConsumerConfiguration
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope
import org.seekerwing.aws.sqsconsumer.model.Queue
import org.seekerwing.aws.sqsconsumer.model.QueueContext
import org.seekerwing.aws.sqsconsumer.sqs.deleteMessage
import software.amazon.awssdk.services.sqs.model.Message

internal class MessageConsumerTest {

    val queue: Queue = mockk(relaxed = true, relaxUnitFun = true)
    val messageProcessor: MessageProcessor = mockk(relaxed = true)
    val channel: ReceiveChannel<MessageEnvelope> = mockk()
    val channelIterator: ChannelIterator<MessageEnvelope> = mockk()

    @Test
    @DisplayName("validate that launchConsumer iterates over ReceiveChannel invokes MessageProcessor.processMessage and Queue.deleteMessage for each Message")
    fun launchConsumer() = runBlockingTest {
        val queueContext = QueueContext(messageProcessor)
        val consumerConfiguration = ConsumerConfiguration(2)
        val messageConsumer = MessageConsumer(consumerConfiguration)

        val message1 = buildMessage(1)
        val message2 = buildMessage(2)
        val message3 = buildMessage(3)
        val message4 = buildMessage(4)
        val message5 = buildMessage(5)
        val message6 = buildMessage(6)

        mockkStatic("org.seekerwing.aws.sqsconsumer.sqs.MessageDeleterKt")

        coEvery { queue.queueContext } returns queueContext
        coEvery { messageProcessor.processMessage(message1) } returns Unit
        coEvery { messageProcessor.processMessage(message2) } throws RuntimeException("processor exploded 💣")
        coEvery { messageProcessor.processMessage(message3) } returns Unit
        coEvery { messageProcessor.processMessage(message4) } returns Unit
        coEvery { messageProcessor.processMessage(message5) } returns Unit
        coEvery { messageProcessor.processMessage(message6) } returns Unit
        coEvery { queue.deleteMessage(message1) } returns Unit
        coEvery { queue.deleteMessage(message2) } returns Unit
        coEvery { queue.deleteMessage(message3) } returns Unit
        coEvery { queue.deleteMessage(message4) } returns Unit
        coEvery { queue.deleteMessage(message5) } throws RuntimeException("queue exploded 💣")
        coEvery { queue.deleteMessage(message6) } returns Unit

        coEvery { channel.iterator() } returns channelIterator
        coEvery { channelIterator.hasNext() } returns
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                false
        coEvery { channelIterator.next() } returns
                MessageEnvelope(message1, queue) andThen
                MessageEnvelope(message2, queue) andThen
                MessageEnvelope(message3, queue) andThen
                MessageEnvelope(message4, queue) andThen
                MessageEnvelope(message5, queue) andThen
                MessageEnvelope(message6, queue)

        messageConsumer.launchConsumer(this, channel)

        coVerify(exactly = 1) { messageProcessor.processMessage(message1) }
        coVerify(exactly = 1) { messageProcessor.processMessage(message2) }
        coVerify(exactly = 1) { messageProcessor.processMessage(message3) }
        coVerify(exactly = 1) { messageProcessor.processMessage(message4) }
        coVerify(exactly = 1) { messageProcessor.processMessage(message5) }
        coVerify(exactly = 1) { messageProcessor.processMessage(message6) }
        coVerify(exactly = 1) { queue.deleteMessage(message1) }
        coVerify(exactly = 0) { queue.deleteMessage(message2) } // messageProcessor threw exception for m2
        coVerify(exactly = 1) { queue.deleteMessage(message3) }
        coVerify(exactly = 1) { queue.deleteMessage(message4) }
        coVerify(exactly = 1) { queue.deleteMessage(message5) }
        coVerify(exactly = 1) { queue.deleteMessage(message6) }
    }

    private fun buildMessage(identifier: Int): Message {
        return Message.builder().body("M$identifier").receiptHandle("R$identifier").build()
    }
}