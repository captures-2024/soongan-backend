package com.soongan.soonganbackend.soongankafka.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfig {

    @Bean
    fun notificationTopic(): NewTopic {
        return NewTopic("soongan-noti", 3, 1)
    }
}