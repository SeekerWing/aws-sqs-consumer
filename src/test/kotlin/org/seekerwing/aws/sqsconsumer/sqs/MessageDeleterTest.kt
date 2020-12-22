package org.seekerwing.aws.sqsconsumer.sqs

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.MessageProcessor
import org.seekerwing.aws.sqsconsumer.model.Queue
import org.seekerwing.aws.sqsconsumer.model.QueueContext
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchResponse
import software.amazon.awssdk.services.sqs.model.Message

internal class MessageDeleterTest {

    val sqsAsyncClient: SqsAsyncClient = mock()

    @Test
    @DisplayName("validate that deleteMessages invokes SQS deleteMessageBatch")
    fun deleteMessages() = runBlockingTest {
        val message: Message = Message
            .builder()
            .body("HelloWorld")
            .receiptHandle("ReceiptHandle")
            .build()
        val deleteMessageBatchRequest: DeleteMessageBatchRequest = buildDeleteMessageBatchRequest(message)

        whenever(sqsAsyncClient.deleteMessageBatch(deleteMessageBatchRequest))
            .thenReturn(
                CompletableFuture.completedFuture(
                    DeleteMessageBatchResponse
                        .builder()
                        .build()))

        val messageProcessor = object : MessageProcessor {
            override suspend fun processMessage(message: Message) {
                println("$message")
            }
        }
        val queue = Queue(sqsAsyncClient, "QUEUE_URL", QueueContext(messageProcessor))
        queue.deleteMessages(setOf(message))

        verify(sqsAsyncClient, times(1)).deleteMessageBatch(deleteMessageBatchRequest)
    }

    private fun buildDeleteMessageBatchRequest(message: Message): DeleteMessageBatchRequest {
        return DeleteMessageBatchRequest.builder()
            .queueUrl("QUEUE_URL")
            .entries(listOf(DeleteMessageBatchRequestEntry.builder()
                .id(message.messageId())
                .receiptHandle(message.receiptHandle())
                .build()))
            .build()
    }
}
