package org.seekerwing.aws.sqsconsumer

import software.amazon.awssdk.services.sqs.model.Message

/**
 * Implementations of the [MessageProcessor] interface define application specific logic. The implementation is
 * expected to be idempotent since both SQS and SQS Consumer Library only guarantee at-least-once delivery.
 * SQS Consumer library invokes the processMessage method for each Message.
 * NOTE: the implementation can avoid retry logic and exception handling to avoid excessive calls to the down streams.
 * The library leverages "Re-drive Policy" and "Visibility Timeout" to solve for retry logic and exception handling.
 */
interface MessageProcessor {

    suspend fun processMessage(message: Message)
}
