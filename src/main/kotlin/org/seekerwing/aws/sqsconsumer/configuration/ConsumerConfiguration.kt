package org.seekerwing.aws.sqsconsumer.configuration

/**
 * [ConsumerConfiguration] provides configuration options for [org.seekerwing.aws.sqsconsumer.MessageConsumer].
 * [parallelism] defines the number of coroutines/parallel executors what will be spun up to invoke
 * [org.seekerwing.aws.sqsconsumer.MessageProcessor.processMessage] for the Messages fetched from the
 * [org.seekerwing.aws.sqsconsumer.model.Queue].
 */
data class ConsumerConfiguration(
    val parallelism: Int = 10
)
