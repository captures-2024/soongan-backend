package com.soongan.soonganbackend.soongankafka.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun sendMessage(topicName: String, message: String) {
        kafkaTemplate.send(topicName, message)
    }
}