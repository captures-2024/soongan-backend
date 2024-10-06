package com.soongan.soonganbackend.soonganredis.config

import com.soongan.soonganbackend.soonganredis.properties.RedisProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate


@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties::class)
class RedisConfig(
    private val properties: RedisProperties
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
//        println("redisHost: ${properties.host}!!!")
        redisStandaloneConfiguration.hostName = properties.host
        redisStandaloneConfiguration.port = properties.port
        redisStandaloneConfiguration.password = RedisPassword.of(properties.password)
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()  // 이 redisTemplate으로 Redis에 접근하여 상호작용(삽입, 수정, 삭제,,) 가능
        redisTemplate.connectionFactory = redisConnectionFactory()
        return redisTemplate
    }
}
