# aws-sqs-consumer

[![Build Status](https://travis-ci.com/SeekerWing/aws-sqs-consumer.svg?branch=master)](https://travis-ci.com/SeekerWing/aws-sqs-consumer)
[![Download](https://api.bintray.com/packages/seekerwing/maven/aws-sqs-consumer/images/download.svg)](https://bintray.com/seekerwing/maven/aws-sqs-consumer/_latestVersion) 
[![Coverage Status](https://codecov.io/gh/SeekerWing/aws-sqs-consumer/branch/master/graph/badge.svg)](https://codecov.io/gh/SeekerWing/aws-sqs-consumer)
[![Code Climate](https://codeclimate.com/github/SeekerWing/aws-sqs-consumer/badges/gpa.svg)](https://codeclimate.com/github/SeekerWing/aws-sqs-consumer)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4b683e64165f48a0a5283f5b26f6cab7)](https://www.codacy.com/app/ray-barunray/aws-sqs-consumer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SeekerWing/aws-sqs-consumer&amp;utm_campaign=Badge_Grade) 
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew13.html) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## Introduction

[Amazon Simple Queue Service](https://aws.amazon.com/sqs/) is a fully managed message queuing service that enables the developer to decouple and scale microservices, distributed systems, and serverless applications. 

The [AWS SQS Consumer](https://github.com/SeekerWing/aws-sqs-consumer) reduces time to launch a SQS Message Consumer, by empowering developers to focus on business logic of consuming the message. 

The "unique selling proposition" of [AWS SQS Consumer](https://github.com/SeekerWing/aws-sqs-consumer) includes:
*   support for priority-based consumption of Messages across multiple Queues 
*   asynchronous (truly non-blocking) implementation to maximize throughput and optimize resource utilization by leveraging [Asynchronous AWS SDK for Java 2.0](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/basics-async.html) and [Kotlin > Coroutines > Channels](https://kotlinlang.org/docs/reference/coroutines/channels.html) 
*   out-of-the-box exception handling via [Dead-Letter Queues](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-dead-letter-queues.html)
*   hides complexity involved in invoking SQS APIs [ReceiveMessage](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_ReceiveMessage.html) and [DeleteMessage](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_DeleteMessage.html)
*   cost reduction because default behavior set to [Long Polling](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-long-polling.html)  

## Design

![design diagram](aws-sqs-consumer.png)

## User Guide

[AWS SQS Consumer](https://github.com/SeekerWing/aws-sqs-consumer) has two public interfaces for the user.
*   [QueueConsumer](https://github.com/SeekerWing/aws-sqs-consumer/blob/master/src/main/kotlin/org/seekerwing/aws/sqsconsumer/QueueConsumer.kt) - framework interface starts/stops the process of polling messages and processing/consuming them
*   [MessageProcessor](https://github.com/SeekerWing/aws-sqs-consumer/blob/master/src/main/kotlin/org/seekerwing/aws/sqsconsumer/MessageProcessor.kt) - implementation hook for the user to implement business logic for a given [Message](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sqs/model/Message.html)

The usage involves implementing a [MessageProcessor](https://github.com/SeekerWing/aws-sqs-consumer/blob/master/src/main/kotlin/org/seekerwing/aws/sqsconsumer/MessageProcessor.kt)
and wiring up the [QueueConsumer](https://github.com/SeekerWing/aws-sqs-consumer/blob/master/src/main/kotlin/org/seekerwing/aws/sqsconsumer/QueueConsumer.kt)

### Implementing [MessageProcessor](https://github.com/SeekerWing/aws-sqs-consumer/blob/master/src/main/kotlin/org/seekerwing/aws/sqsconsumer/MessageProcessor.kt)
```kotlin
import org.apache.logging.log4j.kotlin.Logging
import software.amazon.awssdk.services.sqs.model.Message

class MyAwesomeMessageProcessor : MessageProcessor, Logging {

    override suspend fun processMessage(message: Message) {
        logger.info("my awesome message is ${message.body()}")
        // business logic goes here
    }
}

```

### Wiring Up [QueueConsumer](https://github.com/SeekerWing/aws-sqs-consumer/blob/master/src/main/kotlin/org/seekerwing/aws/sqsconsumer/QueueConsumer.kt)
```kotlin
import org.apache.logging.log4j.kotlin.Logging
import org.seekerwing.aws.sqsconsumer.builder.SingleQueueConsumerBuilder
import org.seekerwing.aws.sqsconsumer.configuration.ConsumerConfiguration
import org.seekerwing.aws.sqsconsumer.configuration.MessageFetcherConfiguration
import org.seekerwing.aws.sqsconsumer.configuration.MessageProviderConfiguration
import org.seekerwing.aws.sqsconsumer.model.Queue
import org.seekerwing.aws.sqsconsumer.model.QueueContext
import software.amazon.awssdk.services.sqs.SqsAsyncClient

class MyAwesomeQueueConsumerApp : Logging {

    private fun execute(args: Array<String>) {
        logger.info("starting app with args ${args.contentToString()}")

        // message processor created in previous step
        val messageProcessor = MyAwesomeMessageProcessor()

        // build queue object with the SQS Client and the Queue URL and the MessageProcessor that must be used to consume messages from the queue
        val queue = Queue(SqsAsyncClient.create(), "https://sqs.us-east-1.amazonaws.com/777777777777/my-awesome-queue", QueueContext(messageProcessor))

        // the parameters indicate number of messages fetched per poll, wait time when polling for messages, and message visibility timeout
        // all the parameters are optional, user may override them as and when necessary or leave them at defaults
        val messageFetcherConfiguration = MessageFetcherConfiguration(10, 20, 300)

        // build message provider configuration with previously built queue and message fetcher configuration and the desired nummber of parallel pollers
        // the message fetcher configuration and the number of parallel pollers configuration are optional, user may override them as and when necessary or leave them at defaults
        val messageProviderConfiguration = MessageProviderConfiguration(queue, messageFetcherConfiguration, 10)

        // build queue consumer with the previously built message provider configuration and consumer configuration (that defines number of parallel message processors)
        // the queue consumer configuration is optional, user may override as and when necessary or leave them at defaults
        val queueConsumer = SingleQueueConsumerBuilder(messageProviderConfiguration, ConsumerConfiguration(50)).build()

        // start queue consumer
        queueConsumer.start()
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { queueConsumer.stop() }))
        // wait for termination
        Thread.currentThread().join()
    }

    companion object {
        fun main(args: Array<String>) {
            MyAwesomeQueueConsumerApp().execute(args)
        }
    }
}
```

## FAQ

#### Q: I don't speak Kotlin, can I still use this library?
A: Kotlin is a JVM based language; the beauty of Kotlin is its interoperability with Java. You can read more about it 
at [Calling Kotlin from Java](https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html) and 
[Calling Java code from Kotlin](https://kotlinlang.org/docs/reference/java-interop.html).

#### Q: How do I handle errors encountered while processing a message?
A: The library recommends that you bubble up your exceptions and let the 
[SQS (re-drive + DLQ)](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-dead-letter-queues.html) 
concept take care of it. Let us do a deep dive here to get a better understanding of what we're proposing.
In the normal course of events, after the [MessageProcessor](https://github.com/SeekerWing/aws-sqs-consumer/blob/master/src/main/kotlin/org/seekerwing/aws/sqsconsumer/MessageProcessor.kt)
returns control to the library (without an exception), the library deletes the message from the queue - thus marking 
successful completion of the message processing. However, when the [MessageProcessor](https://github.com/SeekerWing/aws-sqs-consumer/blob/master/src/main/kotlin/org/seekerwing/aws/sqsconsumer/MessageProcessor.kt)
throws an exception, the library does not delete the message from the queue. Eventually, the visibility timeout of the 
message expires. When the visibility timeout of a message expires before it has been deleted explicitly by a consumer, 
SQS assumes that the consumer has failed to process the message and makes it available to be consumed again. SQS keeps 
on doing so until the maximum re-drive count is reached after which SQS pushes the message to the DLQ. We strongly 
recommend against writing explicit code to move messages to DLQ because it adds a point of failure and complicates the 
code maintenance in the user's codebase.

#### Q: I hear on [AWS Lambda](https://aws.amazon.com/lambda/) I only need to implement my business logic?
A: Yes, you are right. [AWS Lambda](https://aws.amazon.com/lambda/) does the heavy lifting of polling for messages and
handling errors and re-driving messages so that the developer doesn't have to deal with it. You can read more about it at
[AWS Lambda Event Source Mapping](https://docs.aws.amazon.com/lambda/latest/dg/invocation-eventsourcemapping.html) and
[Asynchronous Invocation](https://docs.aws.amazon.com/lambda/latest/dg/invocation-async.html) and
[Error Handling and Automatic Retries in AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/invocation-retries.html).

#### Q: Why do I need this library if [AWS Lambda](https://aws.amazon.com/lambda/) solves for the message processing boilerplate?
A: [AWS Lambda](https://aws.amazon.com/lambda/) is great for most workloads and is evolving gradually to support more
use cases. However, it is fairly common for developers to use other compute platforms for not standard workloads.
These are (but not limited to) [EC2](https://aws.amazon.com/ec2/), [EKS](https://aws.amazon.com/eks/),
[ECS](https://aws.amazon.com/ecs/) and [Fargate](https://aws.amazon.com/fargate/). When using compute platforms
where an external actor is not polling and providing messages to your compute layer, it is the developer's responsibility
to code for SQS polling, deletion and error handling around messages. This library attempts to alleviate that complexity
from the developer, allowing the developer to focus on the business logic of processing/consuming the message.
