package com.soongan.soonganbackend.soonganconsumer.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.soongan.soonganbackend.soonganconsumer.service.fcm.FcmService
import com.soongan.soonganbackend.soongansupport.util.dto.Message
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaNotiConsumer(
    private val fcmService: FcmService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["soongan-noti"], groupId = "soongan-consumer")
    fun consume(message: String) {
        val notificationMessage = objectMapper.readValue(message, Message::class.java)
        fcmService.pushFcmMessage(notificationMessage)
    }
}