package org.seekerwing.aws.sqsconsumer.configuration

/**
 * [ConsumerConfiguration] provides configuration options for [MessageConsumer]
 * [org.seekerwing.aws.sqsconsumer.MessageConsumer].
 *
 * [parallelism] defines the number of coroutines/parallel executors that will be spun up to invoke
 * [MessageProcessor.processMessage][org.seekerwing.aws.sqsconsumer.MessageProcessor.processMessage] for the Messages
 * fetched from the [Queue][org.seekerwing.aws.sqsconsumer.model.Queue].
 */
data class ConsumerConfiguration(
    val parallelism: Int = 10
)
