package org.seekerwing.aws.sqsconsumer.model

import software.amazon.awssdk.services.sqs.SqsAsyncClient

/**
 * Business Object for Queue definition;
 * encapsulates an asynchronous SQS client and queue URL and queue context.
 */
data class Queue(
    val queueClient: SqsAsyncClient,
    val queueUrl: String,
    val queueContext: QueueContext
)
