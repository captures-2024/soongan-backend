package com.soongan.soonganbackend.soonganapi.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.soongan.soonganbackend.soonganapi.service.fcm.FcmService
import com.soongan.soonganbackend.soongansupport.util.dto.Message
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.connection.stream.StreamReadOptions
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisNotiConsumer(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val fcmService: FcmService,
    private val objectMapper: ObjectMapper
) {

    companion object {
        private const val NOTI_STREAM_KEY = "soongan-noti"
        private const val CONSUMER_GROUP = "soongan-consumer-group"
        private const val CONSUMER_NAME = "soongan-consumer"
        private val logger = LoggerFactory.getLogger(RedisNotiConsumer::class.java)
    }

    init {
        createConsumerGroup()
    }

    private fun createConsumerGroup() {
        try {
            redisTemplate.opsForStream<String, Any>()
                .createGroup(NOTI_STREAM_KEY, ReadOffset.from("0"), CONSUMER_GROUP)
            logger.info("Consumer group created: $CONSUMER_GROUP")
        } catch (e: Exception) {
            logger.warn("Consumer group might already exist: $CONSUMER_GROUP")
        }
    }

    @Scheduled(fixedRate = 1000) // 1초마다 메시지 폴링
    fun consumeMessages() {
        try {
            val readOptions = StreamReadOptions.empty()
                .count(10)
                .block(Duration.ofSeconds(1))

            val records = redisTemplate.opsForStream<String, Any>()
                .read(
                    Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
                    readOptions,
                    StreamOffset.create(NOTI_STREAM_KEY, ReadOffset.lastConsumed())
                )

            records?.forEach { record ->
                val message = record.value["message"] as String
                logger.info("Received message: $message")

                // 메시지 처리
                processNotiMessage(message)

                // Redis Streams 메시지에 처리 완료 표시(Ack 상태로 변경)
                redisTemplate.opsForStream<String, Any>()
                    .acknowledge(NOTI_STREAM_KEY, CONSUMER_GROUP, record.id)
            }
        } catch (e: Exception) {
            logger.error("Error consuming messages", e)
        }
    }

    private fun processNotiMessage(message: String) {
        val notificationMessage = objectMapper.readValue(message, Message::class.java)
        logger.info("Processing Noti Message: $notificationMessage")
        fcmService.pushFcmMessage(notificationMessage)
    }
}