package com.soongan.soonganbackend.soonganredis.producer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.connection.stream.StreamRecords
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisMessageProducer(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    fun sendMessage(streamKey: String, message: String) {
        val record = StreamRecords.string(mapOf("message" to message)).withStreamKey(streamKey)
        val recordId = redisTemplate.opsForStream<String, Any>()
            .add(record)
        logger.info("Message sent to $streamKey with recordId: $recordId")
    }
}