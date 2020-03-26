package org.seekerwing.aws.sqsconsumer

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.seekerwing.aws.sqsconsumer.messageprovider.MessageProvider

/**
 * Amazon SQS based implementation of [QueueConsumer].
 */
internal class SqsQueueConsumer(private val provider: MessageProvider, private val consumer: MessageConsumer) :
        CoroutineScope, QueueConsumer {

    private val supervisorJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + supervisorJob

    override fun start() = launch {
        val messageChannel = provider.provideMessages(CoroutineScope(coroutineContext))
        consumer.launchConsumer(CoroutineScope(coroutineContext), messageChannel)
    }

    override fun stop() {
        supervisorJob.cancel()
    }
}
