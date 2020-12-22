package org.seekerwing.aws.sqsconsumer.sqs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.seekerwing.aws.sqsconsumer.configuration.MessageProviderConfiguration
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope

internal fun CoroutineScope.launchMessageFetcher(
    configuration: MessageProviderConfiguration,
    channel: SendChannel<MessageEnvelope>
) = launch {
    while (isActive) {
        try {
            fetchAndProcessMessages(configuration, channel)
        } catch (e: Exception) {
            logger().error("exception fetching message from $configuration.queue", e)
        }
    }
}

private suspend fun fetchAndProcessMessages(
    configuration: MessageProviderConfiguration,
    channel: SendChannel<MessageEnvelope>
) {
    configuration.queue
        .fetchMessage(configuration.messageFetcherConfiguration)
        .chunked(configuration.messageFetcherConfiguration.maxBatchSize)
        .map { it.toSet() }
        .forEach { channel.send(MessageEnvelope(it, configuration.queue)) }
}
