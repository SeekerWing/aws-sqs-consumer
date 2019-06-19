package org.seekerwing.aws.sqsconsumer.configuration

import org.seekerwing.aws.sqsconsumer.model.Queue

/**
 * [MessageProviderConfiguration] provides configuration options for
 * [org.seekerwing.aws.sqsconsumer.messageprovider.MessageProvider].
 * [queue] defines [Queue] from which the [org.seekerwing.aws.sqsconsumer.messageprovider.MessageProvider] will provide
 * messages to the [org.seekerwing.aws.sqsconsumer.MessageProcessor].
 * [messageFetcherConfiguration] defines [MessageProviderConfiguration] to be provide configuration valued for
 * [org.seekerwing.aws.sqsconsumer.sqs.MessageFetcherKt.fetchMessage] to fetch messages from the [Queue].
 * [parallelism] defines the number of coroutines/parallel executors what will be spun up to
 * [org.seekerwing.aws.sqsconsumer.sqs.MessageFetcherKt.fetchMessage] from the provided [Queue].
 */
data class MessageProviderConfiguration(
    val queue: Queue,
    val messageFetcherConfiguration: MessageFetcherConfiguration,
    val parallelism: Int = 1
)
