package com.soongan.soonganbackend.soonganredis.producer

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.connection.stream.StreamRecords
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisMessageProducer(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    fun <T> sendMessage(streamKey: String, message: T) {
        val record = StreamRecords.string(mapOf("message" to objectMapper.writeValueAsString(message))).withStreamKey(streamKey)
        val recordId = redisTemplate.opsForStream<String, Any>()
            .add(record)
        logger.info { "Message sent to $streamKey with recordId: $recordId" }
    }
}