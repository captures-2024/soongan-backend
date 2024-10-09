package com.soongan.soonganbackend.soonganweb.resolver

import com.soongan.soonganbackend.soonganredis.jwt.JwtAdaptor
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
    private val jwtAdaptor: JwtAdaptor,
    private val env: Environment
) {

    @Transactional
    fun issueTokens(userEmail: String, authorities: List<String>): Pair<String, String> {
        val accessToken = createToken(userEmail, authorities, JwtTypeEnum.ACCESS)
        val refreshToken = createToken(userEmail, authorities, JwtTypeEnum.REFRESH)

        jwtAdaptor.save(
            JwtData(
                userEmail = userEmail,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )

        return Pair(accessToken, refreshToken)
    }

    fun createToken(userEmail: String, authorities: List<String>, jwtTypeEnum: JwtTypeEnum): String {
        val claims = Jwts.claims().subject(userEmail)  // Jwt payload에 저장되는 정보
            .add("authorities", authorities)  // 유저의 권한 정보도 저장
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
            when (jwtTypeEnum) {
                JwtTypeEnum.ACCESS -> {
                    jwtAdaptor.findByAccessToken(token)
                        ?: throw SoonganUnauthorizedException(StatusCode.INVALID_JWT_ACCESS_TOKEN)
                }

                JwtTypeEnum.REFRESH -> {
                    jwtAdaptor.findByRefreshToken(token)
                        ?: throw SoonganUnauthorizedException(StatusCode.INVALID_JWT_REFRESH_TOKEN)
                }
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
        val jwtData = jwtAdaptor.findByRefreshToken(refreshToken)
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
        jwtAdaptor.deleteByUserEmail(email)
    }
}
