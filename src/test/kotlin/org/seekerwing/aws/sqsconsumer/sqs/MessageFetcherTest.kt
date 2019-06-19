package org.seekerwing.aws.sqsconsumer.sqs

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.MessageProcessor
import org.seekerwing.aws.sqsconsumer.configuration.MessageFetcherConfiguration
import org.seekerwing.aws.sqsconsumer.model.Queue
import org.seekerwing.aws.sqsconsumer.model.QueueContext
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse
import java.util.concurrent.CompletableFuture

internal class MessageFetcherTest {

    val sqsAsyncClient: SqsAsyncClient = mock()

    @Test
    @DisplayName("validate that fetchMessage invokes SQS receiveMessage")
    fun fetchMessage() = runBlockingTest {
        val receiveMessageRequest: ReceiveMessageRequest = ReceiveMessageRequest
            .builder()
            .queueUrl("QUEUE_URL")
            .maxNumberOfMessages(10)
            .waitTimeSeconds(20)
            .visibilityTimeout(30)
            .build()
        val message: Message = Message
            .builder()
            .body("HelloWorld")
            .build()

        whenever(sqsAsyncClient.receiveMessage(receiveMessageRequest))
            .thenReturn(CompletableFuture.completedFuture(ReceiveMessageResponse
                .builder()
                .messages(message)
                .build()))

        val messageProcessor = object : MessageProcessor {
            override suspend fun processMessage(message: Message) {
                println("$message")
            }
        }
        val queue = Queue(sqsAsyncClient, "QUEUE_URL", QueueContext(messageProcessor))
        val receiveMessage = queue.fetchMessage(MessageFetcherConfiguration())
        assertEquals(listOf(message), receiveMessage)
    }
}
