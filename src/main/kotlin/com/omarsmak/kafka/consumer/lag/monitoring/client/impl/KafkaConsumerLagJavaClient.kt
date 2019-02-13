@file:Suppress("MaximumLineLength", "MaxLineLength")

package com.omarsmak.kafka.consumer.lag.monitoring.client.impl

import com.omarsmak.kafka.consumer.lag.monitoring.client.data.Offsets
import com.omarsmak.kafka.consumer.lag.monitoring.client.exceptions.KafkaConsumerLagClientException
import java.util.Properties
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition

/**
 * An abstraction over Kafka Java clients
 *
 * @author oalsafi
 * @since 2018-09-17
 */

internal class KafkaConsumerLagJavaClient private constructor(props: Properties) : KafkaConsumerLagBaseClient(props) {

    companion object {
        fun create(props: Properties): KafkaConsumerLagJavaClient {
            return KafkaConsumerLagJavaClient(props)
        }
    }

    override fun getConsumerGroupsList(): List<String> {
        val consumerList = javaAdminClient.listConsumerGroups().all().get().map { it.groupId() }
        if (consumerList.isEmpty()) throw KafkaConsumerLagClientException("No consumers existing in the Kafka cluster.")
        return consumerList
    }

    override fun getConsumerOffsets(consumerGroup: String): List<Offsets> {
        val offsets = javaAdminClient.listConsumerGroupOffsets(consumerGroup)
                .partitionsToOffsetAndMetadata()
                .get()
        if (offsets.isEmpty() || offsets == null)
            throw KafkaConsumerLagClientException("Consumer group `$consumerGroup` does not exist in the Kafka cluster.")

        return getConsumerOffsetsPerTopic(offsets)
    }

    private fun getConsumerOffsetsPerTopic(offsets: Map<TopicPartition, OffsetAndMetadata>): List<Offsets> {
        val rawOffsets = mutableMapOf<String, MutableMap<Int, Long>>()
        offsets.forEach { t, u ->
            // First we get the key of the topic
            val offsetPerPartition = rawOffsets.getOrPut(t.topic()) { mutableMapOf() }
            // Add the updated map
            offsetPerPartition.putIfAbsent(t.partition(), u.offset())
            rawOffsets.replace(t.topic(), offsetPerPartition)
        }

        return rawOffsets.map {
            Offsets(it.key, it.value)
        }
    }

    override fun closeClients() {
        // We don't have any specific clients to close here
    }
}