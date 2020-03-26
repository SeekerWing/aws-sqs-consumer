package org.seekerwing.aws.sqsconsumer.sqs

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.MessageProcessor
import org.seekerwing.aws.sqsconsumer.model.Queue
import org.seekerwing.aws.sqsconsumer.model.QueueContext
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse
import software.amazon.awssdk.services.sqs.model.Message

internal class MessageDeleterTest {

    val sqsAsyncClient: SqsAsyncClient = mock()

    @Test
    @DisplayName("validate that deleteMessage invokes SQS deleteMessage")
    @ExperimentalCoroutinesApi
    fun deleteMessage() = runBlockingTest {
        val deleteMessageRequest: DeleteMessageRequest = DeleteMessageRequest
            .builder()
            .queueUrl(QUEUE_URL)
            .receiptHandle(RECEIPT_HANDLE)
            .build()
        val message: Message = Message
            .builder()
            .body("HelloWorld")
            .receiptHandle(RECEIPT_HANDLE)
            .build()
        val deleteMessageResponse: DeleteMessageResponse = DeleteMessageResponse
            .builder()
            .build()

        whenever(sqsAsyncClient.deleteMessage(deleteMessageRequest))
            .thenReturn(CompletableFuture.supplyAsync { deleteMessageResponse })

        val messageProcessor = object : MessageProcessor {
            override suspend fun processMessage(message: Message) {
                println("$message")
            }
        }
        val queue = Queue(sqsAsyncClient, QUEUE_URL, QueueContext(messageProcessor))
        val actualResponse = queue.deleteMessage(message)

        assertEquals(actualResponse, deleteMessageResponse)
        verify(sqsAsyncClient, times(1)).deleteMessage(deleteMessageRequest)
    }

    companion object {
        const val QUEUE_URL = "QUEUE_URL"
        const val RECEIPT_HANDLE = "ReceiptHandle"
    }
}
