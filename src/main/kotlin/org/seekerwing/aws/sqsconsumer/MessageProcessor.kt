package org.seekerwing.aws.sqsconsumer

import software.amazon.awssdk.services.sqs.model.Message

/**
 * Implementations of the [MessageProcessor] interface define application specific logic. The implementation is
 * expected to be idempotent since both SQS and SQS Consumer Library only guarantee at-least-once delivery.
 * SQS Consumer library invokes the processMessage method for each Message.
 * NOTE: the implementation can avoid retry logic and exception handling to avoid excessive calls to the down streams.
 * The library leverages [Re-drive Policy]
 * [https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-dead-letter-queues.html] and
 * [Visibility Timeout]
 * [https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html]
 * to solve for retry logic and exception handling.
 */
interface MessageProcessor {

    /**
     * Processes a set of messages.
     * @return set of messages that could not be processed
     */
    suspend fun processMessages(messages: Set<Message>): Set<Message> {
        messages.forEach { processMessage(it) }
        return emptySet()
    }

    /**
     * Processes a single message. Called by [MessageProcessor.processMessages].
     */
    suspend fun processMessage(message: Message) {
    }
}
