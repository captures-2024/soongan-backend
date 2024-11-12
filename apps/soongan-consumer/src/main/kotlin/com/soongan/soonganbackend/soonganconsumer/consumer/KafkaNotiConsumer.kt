package com.soongan.soonganbackend.soonganconsumer.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.soongan.soonganbackend.soonganconsumer.service.fcm.FcmService
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdaptor
import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationAdaptor
import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationEntity
import com.soongan.soonganbackend.soongansupport.util.dto.Message
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class KafkaNotiConsumer(
    private val fcmService: FcmService,
    private val fcmTokenAdaptor: FcmTokenAdaptor,
    private val notificationAdaptor: NotificationAdaptor,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["soongan-noti"], groupId = "soongan-consumer")
    @Transactional
    fun consume(message: String) {
        val notificationMessage = objectMapper.readValue(message, Message::class.java)
        fcmService.pushFcmMessage(notificationMessage)

        val fcmTokenEntity = fcmTokenAdaptor.findByToken(notificationMessage.token)
        val member = fcmTokenEntity?.member ?: return

        notificationAdaptor.save(NotificationEntity(
            member = member,
            title = notificationMessage.notification.title,
            body = notificationMessage.notification.body
        ))
    }
}