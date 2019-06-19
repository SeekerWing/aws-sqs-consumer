package org.seekerwing.aws.sqsconsumer.messageprovider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import org.seekerwing.aws.sqsconsumer.model.MessageEnvelope

internal interface MessageProvider {

    fun provideMessage(coroutineScope: CoroutineScope): ReceiveChannel<MessageEnvelope>
}
