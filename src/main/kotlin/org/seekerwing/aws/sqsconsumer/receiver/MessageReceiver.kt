package org.seekerwing.aws.sqsconsumer.receiver

import kotlinx.coroutines.future.await
import org.seekerwing.aws.sqsconsumer.model.Queue
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

/**
 * Opinionated message receiver to receive 10 messages SQS with long polling.
 */
class MessageReceiver {

    suspend fun receiveMessage(queue: Queue): List<Message> {
        val receiveMessageRequest: ReceiveMessageRequest = ReceiveMessageRequest
            .builder()
            .queueUrl(queue.queueUrl)
            .maxNumberOfMessages(MAXIMUM_NUMBER_OF_MESSAGES)
            .waitTimeSeconds(WAIT_TIME_SECONDS)
            .build()
        return queue.queueClient
            .receiveMessage(receiveMessageRequest)
            .await()
            .messages()
    }

    companion object {
        const val MAXIMUM_NUMBER_OF_MESSAGES = 10
        const val WAIT_TIME_SECONDS = 20
    }
}
