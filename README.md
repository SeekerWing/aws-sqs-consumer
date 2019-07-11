# aws-sqs-consumer

[![Build Status](https://travis-ci.com/SeekerWing/aws-sqs-consumer.svg?branch=master)](https://travis-ci.com/SeekerWing/aws-sqs-consumer) 
[![Coverage Status](https://codecov.io/gh/SeekerWing/aws-sqs-consumer/branch/master/graph/badge.svg)](https://codecov.io/gh/SeekerWing/aws-sqs-consumer)
[![Code Climate](https://codeclimate.com/github/SeekerWing/aws-sqs-consumer/badges/gpa.svg)](https://codeclimate.com/github/SeekerWing/aws-sqs-consumer)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4b683e64165f48a0a5283f5b26f6cab7)](https://www.codacy.com/app/ray-barunray/aws-sqs-consumer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SeekerWing/aws-sqs-consumer&amp;utm_campaign=Badge_Grade) 
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew13.html) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## Introduction

[Amazon Simple Queue Service](https://aws.amazon.com/sqs/) is a fully managed message queuing service that enables you to decouple and scale microservices, distributed systems, and serverless applications. 

The [AWS SQS Consumer](https://github.com/SeekerWing/aws-sqs-consumer) aims to reduce time to launch a SQS Message Consumer by empowering developers to focus on business logic of processing the message. 

The "unique selling proposition" of [AWS SQS Consumer](https://github.com/SeekerWing/aws-sqs-consumer) are:
*   support for priority based consumption of Messages across multiple Queues 
*   asynchronous (truly non-blocking) implementation at it's core to maximize throughput and optimize resource utilization by leveraging [Asynchronous AWS SDK for Java 2.0](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/basics-async.html) and [Kotlin > Coroutines > Channels](https://kotlinlang.org/docs/reference/coroutines/channels.html) 
*   out of the box exception handling via [Dead-Letter Queues](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-dead-letter-queues.html)
*   abstraction of complexity involved in invoking SQS APIs [ReceiveMessage](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_ReceiveMessage.html) and [DeleteMessage](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_DeleteMessage.html)
*   cost reduction via default behavior set to [Long Polling](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-long-polling.html)  

## Design

## User Guide

## FAQ
