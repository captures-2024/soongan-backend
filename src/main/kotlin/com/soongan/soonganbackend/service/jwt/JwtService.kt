package com.soongan.soonganbackend.service.jwt

import com.soongan.soonganbackend.enums.TokenType
import com.soongan.soonganbackend.interfaces.member.dto.RefreshRequestDto
import com.soongan.soonganbackend.persistence.jwt.JwtAdaptor
import com.soongan.soonganbackend.persistence.jwt.JwtData
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.SoonganUnauthorizedException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtAdaptor: JwtAdaptor,
    private val env: Environment
) {

    @Transactional
    fun issueTokens(userEmail: String, authorities: List<String>): Pair<String, String> {
        val accessToken = createToken(userEmail, authorities, TokenType.ACCESS)
        val refreshToken = createToken(userEmail, authorities, TokenType.REFRESH)

        jwtAdaptor.save(
            JwtData(
                userEmail = userEmail,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )

        return Pair(accessToken, refreshToken)
    }

    fun createToken(userEmail: String, authorities: List<String>, tokenType: TokenType): String {
        val claims = Jwts.claims().subject(userEmail)  // Jwt payload에 저장되는 정보
            .add("authorities", authorities)  // 유저의 권한 정보도 저장
            .build()

        val issuedAt = Date()
        val expiration = when (tokenType) {
            TokenType.ACCESS -> Date(issuedAt.time + 1000 * 60 * 30)  // 30분
            TokenType.REFRESH -> Date(issuedAt.time + 1000 * 60 * 60 * 24 * 14)  // 14일
        }

        return Jwts.builder()  // 토큰 생성
            .claims(claims)
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(getSecretKey())
            .compact()
    }

    @Transactional(readOnly = true)
    fun getPayload(token: String, tokenType: TokenType): Map<String, *> {
        try {
            when (tokenType) {
                TokenType.ACCESS -> {
                    jwtAdaptor.findByAccessToken(token)
                        ?: throw SoonganUnauthorizedException(StatusCode.INVALID_JWT_ACCESS_TOKEN)
                }

                TokenType.REFRESH -> {
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
    fun validateRefreshRequest(refreshRequestDto: RefreshRequestDto): Map<String, *> {
        val jwtData = jwtAdaptor.findByRefreshToken(refreshRequestDto.refreshToken)
            ?: throw SoonganException(StatusCode.INVALID_JWT_REFRESH_TOKEN)

        val payload = getPayload(refreshRequestDto.refreshToken, TokenType.REFRESH)
        if (jwtData.refreshToken != refreshRequestDto.refreshToken ||
            jwtData.accessToken != refreshRequestDto.accessToken ||
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
