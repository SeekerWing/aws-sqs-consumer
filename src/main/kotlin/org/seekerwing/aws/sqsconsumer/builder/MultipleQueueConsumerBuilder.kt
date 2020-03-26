package org.seekerwing.aws.sqsconsumer.builder

import org.seekerwing.aws.sqsconsumer.QueueConsumer
import org.seekerwing.aws.sqsconsumer.configuration.ConsumerConfiguration
import org.seekerwing.aws.sqsconsumer.configuration.MessageProviderConfigurations
import org.seekerwing.aws.sqsconsumer.messageprovider.MessageProvider
import org.seekerwing.aws.sqsconsumer.messageprovider.MultipleQueueBasedMessageProvider

/**
 * Implementation of [QueueConsumerBuilder] to be used by user of the library to build an instance of [QueueConsumer]
 * that is configured to poll multiple queues and invoke the
 * [MessageProcessor][org.seekerwing.aws.sqsconsumer.MessageProcessor] defined for each of the queues as implemented by
 * the user of the library. The builder needs [MessageProviderConfigurations] to instantiate [QueueConsumer] and can
 * optionally accept [ConsumerConfiguration].
 */
class MultipleQueueConsumerBuilder(
    private val messageProviderConfigurations: MessageProviderConfigurations,
    private val consumerConfiguration: ConsumerConfiguration = ConsumerConfiguration()
) : AbstractQueueConsumerBuilder(consumerConfiguration) {

    override fun messageProvider(): MessageProvider = MultipleQueueBasedMessageProvider(messageProviderConfigurations)
}
