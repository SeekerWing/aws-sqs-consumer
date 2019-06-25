package org.seekerwing.aws.sqsconsumer

import kotlinx.coroutines.Job

/**
 * [QueueConsumer] is the external interface for the users of this library. Users are expected to use implementations of
 * [QueueConsumerBuilder][org.seekerwing.aws.sqsconsumer.builder.QueueConsumerBuilder] to build a [QueueConsumer] and
 * then invoke [QueueConsumer.start] to start the process of polling [Queue][org.seekerwing.aws.sqsconsumer.model.Queue].
 * This will trigger the process of polling the [Queue][org.seekerwing.aws.sqsconsumer.model.Queue] for messages and
 * invoke the provided implementation of [MessageProcessor][org.seekerwing.aws.sqsconsumer.MessageProcessor] to process
 * the message and then delete the message from the [Queue][org.seekerwing.aws.sqsconsumer.model.Queue]. Users need to
 * invoke [QueueConsumer.stop] to stop the process of polling processing and deleting the message.
 */
interface QueueConsumer {

    fun start(): Job

    fun stop()
}
