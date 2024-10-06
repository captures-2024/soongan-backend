package com.soongan.soonganbackend.soonganredis.jwt

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable

@RedisHash(value = "jwt", timeToLive = 60 * 60 * 24 * 14)  // refresh token의 만료 시간은 14일
data class JwtData(
    @Id val userEmail: String,
    @Indexed val accessToken: String,
    @Indexed val refreshToken: String  // refresh token을 인덱스로 설정하여 검색 시간 단축
): Serializable
