package com.omarsmak.kafka.consumer.lag.monitoring.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotEquals

internal class KafkaConsumerLagClientConfigTest {
    @Test
    fun `test if default configurations being initialized`(){
        val config = KafkaConsumerLagClientConfig.create(mapOf(
                KafkaConsumerLagClientConfig.BOOTSTRAP_SERVERS to "kafka1:9092,kafka2:9092",
                KafkaConsumerLagClientConfig.CONSUMER_GROUPS to setOf("consumer_1", "consumer_2")
        ))

        assertEquals("kafka1:9092,kafka2:9092", config[KafkaConsumerLagClientConfig.BOOTSTRAP_SERVERS])
        assertEquals(setOf("consumer_1", "consumer_2"), config[KafkaConsumerLagClientConfig.CONSUMER_GROUPS])
        assertEquals(9000, config[KafkaConsumerLagClientConfig.HTTP_PORT])
        assertEquals(200, config[KafkaConsumerLagClientConfig.POLL_INTERVAL])
        assertEquals("java", config[KafkaConsumerLagClientConfig.CLIENT_TYPE])
        assertEquals(500, config[KafkaConsumerLagClientConfig.LAG_THRESHOLD])

        // Test the type safe configs
        assertNotEquals("9000", config[KafkaConsumerLagClientConfig.HTTP_PORT])
        assertNotEquals("200", config[KafkaConsumerLagClientConfig.POLL_INTERVAL])
        assertNotEquals("500", config[KafkaConsumerLagClientConfig.LAG_THRESHOLD])
    }

    @Test
    fun `test if config values overrides the defaults`(){
        val config = KafkaConsumerLagClientConfig.create(mapOf(
                KafkaConsumerLagClientConfig.BOOTSTRAP_SERVERS to "kafka1:9092,kafka2:9092",
                KafkaConsumerLagClientConfig.CONSUMER_GROUPS to setOf("consumer_1", "consumer_2"),
                KafkaConsumerLagClientConfig.POLL_INTERVAL to 100,
                KafkaConsumerLagClientConfig.HTTP_PORT to 3000
        ))

        assertEquals("kafka1:9092,kafka2:9092", config[KafkaConsumerLagClientConfig.BOOTSTRAP_SERVERS])
        assertEquals(setOf("consumer_1", "consumer_2"), config[KafkaConsumerLagClientConfig.CONSUMER_GROUPS])
        assertEquals(3000, config[KafkaConsumerLagClientConfig.HTTP_PORT])
        assertEquals(100, config[KafkaConsumerLagClientConfig.POLL_INTERVAL])
    }

    @Test
    fun `test if we throw an exception in case of invalid config or missing required configs`(){
        assertThrows<KafkaConsumerLagClientConfigException>("Should throw an exception for invalid type config"){
            KafkaConsumerLagClientConfig.create(mapOf(
                    KafkaConsumerLagClientConfig.BOOTSTRAP_SERVERS to "kafka1:9092,kafka2:9092",
                    KafkaConsumerLagClientConfig.CONSUMER_GROUPS to setOf("consumer_1", "consumer_2"),
                    KafkaConsumerLagClientConfig.POLL_INTERVAL to "100",
                    KafkaConsumerLagClientConfig.HTTP_PORT to "300000"
            ))
        }

        assertThrows<KafkaConsumerLagClientConfigException> ("Should throw an exception for missing required configs") {
            KafkaConsumerLagClientConfig.create(mapOf(
                    KafkaConsumerLagClientConfig.POLL_INTERVAL to "100",
                    KafkaConsumerLagClientConfig.HTTP_PORT to "300000"
            ))
        }
    }

    @Test
    fun `test if we throw an exception in case of invalid key`(){
        val config = KafkaConsumerLagClientConfig.create(mapOf(
                KafkaConsumerLagClientConfig.BOOTSTRAP_SERVERS to "kafka1:9092,kafka2:9092",
                KafkaConsumerLagClientConfig.CONSUMER_GROUPS to setOf("consumer_1", "consumer_2"),
                KafkaConsumerLagClientConfig.POLL_INTERVAL to 100,
                KafkaConsumerLagClientConfig.HTTP_PORT to 3000
        ))

        assertThrows<KafkaConsumerLagClientConfigException> ("Should throw an exception for an invalid key"){
            config["dummy_config"]
        }
    }
}