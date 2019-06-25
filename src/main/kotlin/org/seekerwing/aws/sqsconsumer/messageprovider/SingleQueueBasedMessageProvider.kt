package org.seekerwing.aws.sqsconsumer.messageprovider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import org.seekerwing.aws.sqsconsumer.configuration.MessageProviderConfiguration
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope
import org.seekerwing.aws.sqsconsumer.sqs.launchMessageFetcher

internal class SingleQueueBasedMessageProvider(
    private val configuration: MessageProviderConfiguration
) : MessageProvider {

    override fun provideMessages(coroutineScope: CoroutineScope) = coroutineScope.produce<MessageEnvelope> {
        repeat(configuration.parallelism) {
            launchMessageFetcher(configuration, channel)
        }
    }
}
