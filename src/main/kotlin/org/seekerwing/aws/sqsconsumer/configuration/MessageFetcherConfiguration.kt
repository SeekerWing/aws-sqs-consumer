package org.seekerwing.aws.sqsconsumer.configuration

/**
 * [MessageFetcherConfiguration] provides configuration options for
 * [MessageFetcherKt.fetchMessage][org.seekerwing.aws.sqsconsumer.sqs.MessageFetcherKt.fetchMessage].
 *
 * [maximumNumberOfMessages] indicates how many messages should be fetched from the queue in a single request;
 * valid values: 1 to 10;.
 *
 * [waitTimeSeconds] indicates duration (in seconds) for which the call waits for a message to arrive in the queue
 * before returning. If a message is available, the call returns sooner than [waitTimeSeconds]. If no messages are
 * available and the wait time expires, the call returns successfully with an empty list of messages. It is recommended
 * that the value be set to the maximum possible value to reduces costs when there are no messages present in the queue;
 * valid values: 0 to 20;.
 *
 * [visibilityTimeoutSeconds] indicates duration (in seconds) that the message is hidden from subsequent fetch requests
 * after being fetched;
 * valid values: 0 to 43200;.
 */
data class MessageFetcherConfiguration(
    val maximumNumberOfMessages: Int,
    val waitTimeSeconds: Int,
    val visibilityTimeoutSeconds: Int
) {

    @Deprecated(
        message = "Please specify values explicitly",
        replaceWith = ReplaceWith(
            expression = "MessageFetcherConfiguration(/* TODO revisit parameters*/ maximumNumberOfMessages = 10, " +
                    "waitTimeSeconds = 20, visibilityTimeoutSeconds = 30)"))
    @Suppress("MagicNumber")
    constructor() : this (10, 20, 30)
}
