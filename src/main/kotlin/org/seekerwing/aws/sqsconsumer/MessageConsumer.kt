package org.seekerwing.aws.sqsconsumer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.Logging
import org.seekerwing.aws.sqsconsumer.configuration.ConsumerConfiguration
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope
import org.seekerwing.aws.sqsconsumer.sqs.deleteMessages

/**
 * [MessageConsumer] consumes [MessageEnvelope] from the [ReceiveChannel] populated by the
 * [org.seekerwing.aws.sqsconsumer.messageprovider.MessageProvider]. It spins up N coroutines as specified in
 * [ConsumerConfiguration.parallelism] to consume [MessageEnvelope]. The coroutine job is a while loop that reads
 * from the [ReceiveChannel] and invokes [MessageProcessor.processMessage] associated with the
 * [MessageEnvelope.sourceQueue]. If the [MessageProcessor.processMessage] completes successfully the consumer invokes
 * [org.seekerwing.aws.sqsconsumer.model.Queue.deleteMessage] which serves a ACK by removing message from Queue
 * thus marking successful completion of the processing of the incoming message.
 */
internal open class MessageConsumer(private val configuration: ConsumerConfiguration) {

    fun launchConsumer(coroutineScope: CoroutineScope, channel: ReceiveChannel<MessageEnvelope>) {
        repeat(configuration.parallelism) { launchSingleConsumer(coroutineScope, channel) }
    }

    private fun launchSingleConsumer(coroutineScope: CoroutineScope, channel: ReceiveChannel<MessageEnvelope>) =
            coroutineScope.launch {
                for (messageEnvelope in channel) {
                    try {
                        val failedMessages =
                            messageEnvelope
                                .sourceQueue
                                .queueContext
                                .messageProcessor
                                .processMessages(messageEnvelope.messages)

                        val messagesToDelete = messageEnvelope.messages - failedMessages
                        if (messagesToDelete.isNotEmpty()) {
                            messageEnvelope.sourceQueue.deleteMessages(messagesToDelete)
                        }
                    } catch (e: Exception) {
                        logger.error("exception processing message ${messageEnvelope.messages}", e)
                    }
                }
    }

    companion object : Logging
}
