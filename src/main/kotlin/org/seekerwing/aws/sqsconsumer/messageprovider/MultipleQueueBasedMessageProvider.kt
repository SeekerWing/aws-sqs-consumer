package org.seekerwing.aws.sqsconsumer.messageprovider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import org.seekerwing.aws.sqsconsumer.configuration.MessageProviderConfigurations
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope
import org.seekerwing.aws.sqsconsumer.sqs.launchMessageFetcher

internal class MultipleQueueBasedMessageProvider(
    private val configurations: MessageProviderConfigurations
) : MessageProvider {

    override fun provideMessage(coroutineScope: CoroutineScope) = coroutineScope.produce<MessageEnvelope> {
        configurations.forEach { configuration ->
            repeat(configuration.parallelism) {
                launchMessageFetcher(configuration, channel)
            }
        }
    }
}
