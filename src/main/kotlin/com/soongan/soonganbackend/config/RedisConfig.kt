package com.soongan.soonganbackend.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate


@Configuration
@EnableCaching
class RedisConfig {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory("localhost", 6379)  // 추후 비밀 정보로 환경변수 설정 필요
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()  // 이 redisTemplate으로 Redis에 접근하여 상호작용(삽입, 수정, 삭제,,) 가능
        redisTemplate.connectionFactory = redisConnectionFactory()
        return redisTemplate
    }
}