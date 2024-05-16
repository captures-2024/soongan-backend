package com.soongan.soonganbackend.persistence.member

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash(value = "jwt", timeToLive = 60 * 60 * 24 * 14)  // refresh token의 만료 시간은 14일
data class JwtData(
    @Id val userEmail: String,
    val accessToken: String,
    val refreshToken: String
): Serializable