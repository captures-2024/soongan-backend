package com.soongan.soonganbackend.soongansupport.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper() = ObjectMapper().apply {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        registerModules(
            KotlinModule.Builder().build(),
            JavaTimeModule(), // JavaTimeModule 등록 - LocalDateTime 등 Java 8 날짜/시간 타입 지원을 위해 필요
            SimpleModule().apply {
                addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(formatter))
                addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(formatter))
            }
        )

        // 날짜를 타임스탬프가 아닌 ISO-8601 형식으로 직렬화
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        // 기존 설정 유지
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
    }
}