package com.soongan.soonganbackend.soonganredis.properties

import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("spring.data.redis")
@Validated
data class RedisProperties (
    @NotEmpty
    val host: String = "hello redis",
    val port: Int = 0,
    @NotEmpty
    val password: String = ""
)
