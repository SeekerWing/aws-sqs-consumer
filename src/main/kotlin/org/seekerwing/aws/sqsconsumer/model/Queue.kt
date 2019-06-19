package org.seekerwing.aws.sqsconsumer.model

import software.amazon.awssdk.services.sqs.SqsAsyncClient

/**
 * DO for Queue definition, encapsulates an asynchronous SQS client and queue URL.
 */
data class Queue(
    val queueClient: SqsAsyncClient,
    val queueUrl: String
)
