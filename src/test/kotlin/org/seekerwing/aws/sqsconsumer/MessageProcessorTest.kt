package org.seekerwing.aws.sqsconsumer

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.sqs.model.Message

class MessageProcessorTest {

    @Test
    fun `process messages calls process message`() {
        val messages = mutableSetOf<Message>()
        val messageProcessor = object : MessageProcessor {
            override suspend fun processMessage(message: Message) {
                messages.add(message)
            }
        }

        val inputMessages = setOf(Message.builder().body("M1").build(), Message.builder().body("M2").build())

        runBlocking { messageProcessor.processMessages(inputMessages) }

        assertEquals(messages, inputMessages)
    }
}
