package org.seekerwing.aws.sqsconsumer.messageprovider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope

internal interface MessageProvider {

    fun provideMessages(coroutineScope: CoroutineScope): ReceiveChannel<MessageEnvelope>
}
