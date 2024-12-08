package com.soongan.soonganbackend.soonganredis.producer

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.StreamRecords
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisMessageProducer(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    companion object {
        private val logger = LoggerFactory.getLogger(RedisMessageProducer::class.java)
    }

    fun sendMessage(streamKey: String, message: String) {
        val record = StreamRecords.string(mapOf("message" to message)).withStreamKey(streamKey)
        val recordId = redisTemplate.opsForStream<String, Any>()
            .add(record)
        logger.info("Message sent to $streamKey with recordId: $recordId")
    }
}