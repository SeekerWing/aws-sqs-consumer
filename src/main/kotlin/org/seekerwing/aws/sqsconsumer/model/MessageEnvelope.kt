package org.seekerwing.aws.sqsconsumer.model

import software.amazon.awssdk.services.sqs.model.Message

/**
 * Business Object for Message Envelope;
 * encapsulates [Message] and associated [Queue].
 */
internal data class MessageEnvelope(
    val message: Message,
    val sourceQueue: Queue
)
