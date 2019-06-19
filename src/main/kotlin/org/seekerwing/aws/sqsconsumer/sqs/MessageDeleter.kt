package org.seekerwing.aws.sqsconsumer.sqs

import kotlinx.coroutines.future.await
import org.seekerwing.aws.sqsconsumer.model.Queue
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.Message

suspend fun Queue.deleteMessage(message: Message) {
    val deleteMessageRequest: DeleteMessageRequest = DeleteMessageRequest
        .builder()
        .queueUrl(queueUrl)
        .receiptHandle(message.receiptHandle())
        .build()
    queueClient
        .deleteMessage(deleteMessageRequest)
        .await()
}
