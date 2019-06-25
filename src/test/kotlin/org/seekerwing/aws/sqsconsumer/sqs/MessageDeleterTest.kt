package org.seekerwing.aws.sqsconsumer.sqs

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.MessageProcessor
import org.seekerwing.aws.sqsconsumer.model.Queue
import org.seekerwing.aws.sqsconsumer.model.QueueContext
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse
import software.amazon.awssdk.services.sqs.model.Message
import java.util.concurrent.CompletableFuture

internal class MessageDeleterTest {

    val sqsAsyncClient: SqsAsyncClient = mock()

    @Test
    @DisplayName("validate that deleteMessage invokes SQS deleteMessage")
    fun deleteMessage() = runBlockingTest {
        val deleteMessageRequest: DeleteMessageRequest = DeleteMessageRequest
            .builder()
            .queueUrl("QUEUE_URL")
            .receiptHandle("ReceiptHandle")
            .build()
        val message: Message = Message
            .builder()
            .body("HelloWorld")
            .receiptHandle("ReceiptHandle")
            .build()

        whenever(sqsAsyncClient.deleteMessage(deleteMessageRequest))
            .thenReturn(
                CompletableFuture.completedFuture(
                    DeleteMessageResponse
                        .builder()
                        .build()))

        val messageProcessor = object : MessageProcessor {
            override suspend fun processMessage(message: Message) {
                println("$message")
            }
        }
        val queue = Queue(sqsAsyncClient, "QUEUE_URL", QueueContext(messageProcessor))
        queue.deleteMessage(message)

        verify(sqsAsyncClient, times(1)).deleteMessage(deleteMessageRequest)
    }
}