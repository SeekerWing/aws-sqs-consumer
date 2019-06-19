package org.seekerwing.aws.sqsconsumer.receiver

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.seekerwing.aws.sqsconsumer.model.Queue
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse
import java.util.concurrent.CompletableFuture

internal class MessageReceiverTest {

    val messageReceiver: MessageReceiver = MessageReceiver()

    val sqsAsyncClient: SqsAsyncClient = mock()

    @Test
    fun receiveMessage() = runBlocking<Unit> {
        val receiveMessageRequest: ReceiveMessageRequest = ReceiveMessageRequest
            .builder()
            .queueUrl("QUEUE_URL")
            .maxNumberOfMessages(MessageReceiver.MAXIMUM_NUMBER_OF_MESSAGES)
            .waitTimeSeconds(MessageReceiver.WAIT_TIME_SECONDS)
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

        val receiveMessage = messageReceiver.receiveMessage(Queue(sqsAsyncClient, "QUEUE_URL"))
        assertEquals(listOf(message), receiveMessage)
    }
}
