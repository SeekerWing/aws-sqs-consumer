package org.seekerwing.aws.sqsconsumer.sqs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.seekerwing.aws.sqsconsumer.configuration.MessageProviderConfiguration
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope
import software.amazon.awssdk.services.sqs.model.Message

internal fun CoroutineScope.launchMessageFetcher(
    configuration: MessageProviderConfiguration,
    channel: SendChannel<MessageEnvelope>
) = launch {
    while (isActive) {
        try {
            configuration.queue
                .fetchMessage(configuration.messageFetcherConfiguration)
                .forEach { message: Message ->
                    println(message)
                    channel.send(MessageEnvelope(message, configuration.queue))
                }
        } catch (e: Exception) {
            logger().error("exception fetching message from $configuration.queue", e)
        }
    }
}
