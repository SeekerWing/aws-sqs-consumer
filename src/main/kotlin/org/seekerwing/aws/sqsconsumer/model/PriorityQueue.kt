package org.seekerwing.aws.sqsconsumer.model

/**
 * DO for Priority Queue definition, encapsulates a Queue and associated Priority.
 * Higher the priority indicates more important queue.
 */
data class PriorityQueue(
    val queue: Queue,
    val priority: Int
)
