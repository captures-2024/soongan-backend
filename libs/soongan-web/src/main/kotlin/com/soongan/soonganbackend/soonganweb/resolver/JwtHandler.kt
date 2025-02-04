package com.soongan.soonganbackend.soonganweb.resolver

import com.soongan.soonganbackend.soonganredis.jwt.JwtAdapter
import com.soongan.soonganbackend.soonganredis.jwt.JwtData
import com.soongan.soonganbackend.soonganredis.jwt.JwtTypeEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganUnauthorizedException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtHandler(
    private val jwtAdapter: JwtAdapter,
    private val env: Environment
) {

    @Transactional
    fun issueTokens(userEmail: String): Pair<String, String> {
        val accessToken = createToken(userEmail, JwtTypeEnum.ACCESS)
        val refreshToken = createToken(userEmail, JwtTypeEnum.REFRESH)

        jwtAdapter.save(
            JwtData(
                userEmail = userEmail,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )

        return Pair(accessToken, refreshToken)
    }

    fun createToken(userEmail: String, jwtTypeEnum: JwtTypeEnum): String {
        val claims = Jwts.claims()  // Jwt payload에 저장되는 정보
            .subject(userEmail)
            .build()

        val issuedAt = Date()
        val expiration = when (jwtTypeEnum) {
            JwtTypeEnum.ACCESS -> Date(issuedAt.time + 1000 * 60 * 30)  // 30분
            JwtTypeEnum.REFRESH -> Date(issuedAt.time + 1000 * 60 * 60 * 24 * 14)  // 14일
        }

        return Jwts.builder()  // 토큰 생성
            .claims(claims)
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(getSecretKey())
            .compact()
    }

    @Transactional(readOnly = true)
    fun getPayload(token: String, jwtTypeEnum: JwtTypeEnum): Map<String, *> {
        try {
            if (jwtTypeEnum == JwtTypeEnum.REFRESH) {
                jwtAdapter.findByRefreshToken(token)
                    ?: throw SoonganUnauthorizedException(StatusCode.INVALID_JWT_REFRESH_TOKEN)
            }

            return generateValidatedPayload(token)
        } catch (e: JwtException) {
            throw SoonganUnauthorizedException(StatusCode.INVALID_JWT)
        }
    }

    private fun generateValidatedPayload(token: String): Claims {
        val payload: Claims = Jwts.parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token)
            .payload

        return if (payload.expiration.after(Date())) {
            payload
        } else {
            throw SoonganUnauthorizedException(StatusCode.EXPIRED_JWT)
        }
    }

    @Transactional(readOnly = true)
    fun validateRefreshRequest(accessToken: String, refreshToken: String): Map<String, *> {
        val jwtData = jwtAdapter.findByRefreshToken(refreshToken)
            ?: throw SoonganException(StatusCode.INVALID_JWT_REFRESH_TOKEN)

        val payload = getPayload(refreshToken, JwtTypeEnum.REFRESH)
        if (jwtData.refreshToken != refreshToken ||
            jwtData.accessToken != accessToken ||
            jwtData.userEmail != payload["sub"]) {
            throw SoonganException(StatusCode.INVALID_JWT_REFRESH_TOKEN)
        }
        return payload
    }

    private fun getSecretKey(): SecretKey {
        val secretKey = env.getProperty("jwt.secret")!!
        return Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    @Transactional
    fun deleteToken(email: String) {
        jwtAdapter.deleteByUserEmail(email)
    }
}
