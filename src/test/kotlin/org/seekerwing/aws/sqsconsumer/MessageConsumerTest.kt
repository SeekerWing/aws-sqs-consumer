package org.seekerwing.aws.sqsconsumer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.configuration.ConsumerConfiguration
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope
import org.seekerwing.aws.sqsconsumer.model.Queue
import org.seekerwing.aws.sqsconsumer.model.QueueContext
import org.seekerwing.aws.sqsconsumer.sqs.deleteMessages
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchResponse
import software.amazon.awssdk.services.sqs.model.Message

internal class MessageConsumerTest {

    val queue: Queue = mockk(relaxed = true, relaxUnitFun = true)
    val messageProcessor: MessageProcessor = mockk(relaxed = true)
    val channel: ReceiveChannel<MessageEnvelope> = mockk()
    val channelIterator: ChannelIterator<MessageEnvelope> = mockk()

    @Test
    @DisplayName("validate that launchConsumer iterates over ReceiveChannel invokes MessageProcessor.processMessages and Queue.deleteMessages for each Message batch")
    fun launchConsumer() = runBlockingTest {
        val queueContext = QueueContext(messageProcessor)
        val consumerConfiguration = ConsumerConfiguration(2)
        val messageConsumer = MessageConsumer(consumerConfiguration)
        val deleteBatchResponse = DeleteMessageBatchResponse.builder().build()

        val messageBatch1 = setOf(buildMessage(1), buildMessage(2), buildMessage(3))
        val messageBatch2 = setOf(buildMessage(4))
        val messageBatch3 = setOf(buildMessage(5), buildMessage(6))
        val messageBatch4 = setOf(buildMessage(7), buildMessage(8), buildMessage(9))
        val messageBatch5 = emptySet<Message>()

        mockkStatic("org.seekerwing.aws.sqsconsumer.sqs.MessageDeleterKt")

        coEvery { queue.queueContext } returns queueContext
        coEvery { messageProcessor.processMessages(messageBatch1) } returns emptySet()
        coEvery { messageProcessor.processMessages(messageBatch2) } throws RuntimeException("processor exploded 💣")
        coEvery { messageProcessor.processMessages(messageBatch3) } returns emptySet()
        coEvery { messageProcessor.processMessages(messageBatch4) } returns setOf(buildMessage(8))
        coEvery { messageProcessor.processMessages(messageBatch5) } returns emptySet()
        coEvery { queue.deleteMessages(messageBatch1) } returns deleteBatchResponse
        coEvery { queue.deleteMessages(messageBatch2) } returns deleteBatchResponse
        coEvery { queue.deleteMessages(messageBatch3) } throws RuntimeException("queue exploded 💣")
        coEvery { queue.deleteMessages(messageBatch4.minus(buildMessage(8))) } returns deleteBatchResponse
        coEvery { queue.deleteMessages(messageBatch5) } returns deleteBatchResponse

        coEvery { channel.iterator() } returns channelIterator
        coEvery { channelIterator.hasNext() } returns
                true andThen
                true andThen
                true andThen
                true andThen
                true andThen
                false
        coEvery { channelIterator.next() } returns
                MessageEnvelope(messageBatch1, queue) andThen
                MessageEnvelope(messageBatch2, queue) andThen
                MessageEnvelope(messageBatch3, queue) andThen
                MessageEnvelope(messageBatch4, queue) andThen
                MessageEnvelope(messageBatch5, queue)

        messageConsumer.launchConsumer(this, channel)

        coVerify(exactly = 1) { messageProcessor.processMessages(messageBatch1) }
        coVerify(exactly = 1) { messageProcessor.processMessages(messageBatch2) }
        coVerify(exactly = 1) { messageProcessor.processMessages(messageBatch3) }
        coVerify(exactly = 1) { messageProcessor.processMessages(messageBatch4) }
        coVerify(exactly = 1) { messageProcessor.processMessages(messageBatch5) }
        coVerify(exactly = 1) { queue.deleteMessages(messageBatch1) }
        // no delete because of exception in processing entire batch
        coVerify(exactly = 0) { queue.deleteMessages(messageBatch2) }
        coVerify(exactly = 1) { queue.deleteMessages(messageBatch3) }
        // partial delete as one message failed in processing
        coVerify(exactly = 1) { queue.deleteMessages(messageBatch4.minus(buildMessage(8))) }
        // no delete because empty input message set
        coVerify(exactly = 0) { queue.deleteMessages(messageBatch5) }
    }

    private fun buildMessage(identifier: Int): Message {
        return Message.builder().body("M$identifier").receiptHandle("R$identifier").build()
    }
}
