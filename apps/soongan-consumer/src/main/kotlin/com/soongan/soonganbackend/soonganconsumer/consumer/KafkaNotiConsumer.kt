package com.soongan.soonganbackend.soonganconsumer.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaNotiConsumer {

    @KafkaListener(topics = ["soongan-noti"], groupId = "soongan-consumer")
    fun consume(message: String) {
        println("Consumed message: $message")
    }
}