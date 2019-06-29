package org.seekerwing.aws.sqsconsumer.model

/**
 * Business Object for Priority Queue definition;
 * encapsulates a Queue and associated Priority. Higher the priority indicates more important queue.
 */
data class PriorityQueue(
    val queue: Queue,
    val priority: Int
)
