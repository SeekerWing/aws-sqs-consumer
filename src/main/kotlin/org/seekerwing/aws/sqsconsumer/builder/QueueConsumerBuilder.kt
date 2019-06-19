package org.seekerwing.aws.sqsconsumer.builder

import org.seekerwing.aws.sqsconsumer.QueueConsumer

internal interface QueueConsumerBuilder {

    fun build(): QueueConsumer
}
