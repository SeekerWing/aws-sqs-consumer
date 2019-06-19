package org.seekerwing.aws.sqsconsumer.model

import org.seekerwing.aws.sqsconsumer.MessageProcessor

/**
 * Business Object for Queue Context;
 * encapsulates [MessageProcessor] used to process messages in the [org.seekerwing.aws.sqsconsumer.model.Queue]
 */
data class QueueContext(
    val messageProcessor: MessageProcessor
)
