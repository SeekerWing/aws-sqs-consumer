package org.seekerwing.aws.sqsconsumer.sqs

import kotlinx.coroutines.future.await
import org.seekerwing.aws.sqsconsumer.configuration.MessageFetcherConfiguration
import org.seekerwing.aws.sqsconsumer.model.Queue
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

suspend fun Queue.fetchMessage(configuration: MessageFetcherConfiguration): List<Message> {
    val receiveMessageRequest: ReceiveMessageRequest = ReceiveMessageRequest
        .builder()
        .queueUrl(queueUrl)
        .maxNumberOfMessages(configuration.maximumNumberOfMessages)
        .waitTimeSeconds(configuration.waitTimeSeconds)
        .visibilityTimeout(configuration.visibilityTimeoutSeconds)
        .build()
    return queueClient
        .receiveMessage(receiveMessageRequest)
        .await()
        .messages()
}
