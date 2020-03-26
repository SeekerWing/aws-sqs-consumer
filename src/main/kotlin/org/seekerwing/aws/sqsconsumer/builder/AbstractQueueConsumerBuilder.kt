package org.seekerwing.aws.sqsconsumer.builder

import org.seekerwing.aws.sqsconsumer.MessageConsumer
import org.seekerwing.aws.sqsconsumer.QueueConsumer
import org.seekerwing.aws.sqsconsumer.SqsQueueConsumer
import org.seekerwing.aws.sqsconsumer.configuration.ConsumerConfiguration
import org.seekerwing.aws.sqsconsumer.messageprovider.MessageProvider

/**
 * Base implementation of [QueueConsumerBuilder] used by builders providing multiple variations of QueueConsumers.
 * Currently supported queue consumers are [MultipleQueueConsumerBuilder] and [SingleQueueConsumerBuilder].
 */
abstract class AbstractQueueConsumerBuilder(
    private val consumerConfiguration: ConsumerConfiguration = ConsumerConfiguration()
) : QueueConsumerBuilder {

    internal abstract fun messageProvider(): MessageProvider

    override fun build(): QueueConsumer {
        val messageProvider = messageProvider()
        val messageConsumer = MessageConsumer(consumerConfiguration)
        return SqsQueueConsumer(messageProvider, messageConsumer)
    }
}
