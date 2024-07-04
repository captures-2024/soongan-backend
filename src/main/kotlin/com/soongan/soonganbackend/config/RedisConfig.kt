package com.soongan.soonganbackend.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate


@Configuration
@EnableCaching
class RedisConfig(
    private val env: Environment
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = env.getProperty("redis.host")!!
        redisStandaloneConfiguration.port = env.getProperty("redis.port")!!.toInt()
        redisStandaloneConfiguration.password = RedisPassword.of(env.getProperty("redis.password")!!)
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()  // 이 redisTemplate으로 Redis에 접근하여 상호작용(삽입, 수정, 삭제,,) 가능
        redisTemplate.connectionFactory = redisConnectionFactory()
        return redisTemplate
    }
}