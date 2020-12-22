package org.seekerwing.aws.sqsconsumer.model

import software.amazon.awssdk.services.sqs.model.Message

/**
 * Business Object for Message Envelope;
 * encapsulates [Set] of [Message]s and associated [Queue].
 */
internal data class MessageEnvelope(
    val messages: Set<Message>,
    val sourceQueue: Queue
)
