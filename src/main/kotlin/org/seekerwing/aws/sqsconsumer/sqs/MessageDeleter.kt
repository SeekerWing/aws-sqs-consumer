package org.seekerwing.aws.sqsconsumer.sqs

import kotlinx.coroutines.future.await
import org.seekerwing.aws.sqsconsumer.model.Queue
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchResponse
import software.amazon.awssdk.services.sqs.model.Message

suspend fun Queue.deleteMessages(messages: Set<Message>): DeleteMessageBatchResponse {
    val deleteMessageBatchRequest: DeleteMessageBatchRequest = DeleteMessageBatchRequest
        .builder()
        .queueUrl(queueUrl)
        .entries(buildEntries(messages))
        .build()
    return queueClient
        .deleteMessageBatch(deleteMessageBatchRequest)
        .await()
}

private fun buildEntries(messages: Set<Message>): List<DeleteMessageBatchRequestEntry> {
    return messages.map {
        DeleteMessageBatchRequestEntry.builder()
            .id(it.messageId())
            .receiptHandle(it.receiptHandle())
            .build()
    }
}
