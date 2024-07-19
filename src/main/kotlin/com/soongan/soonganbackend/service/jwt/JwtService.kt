package com.soongan.soonganbackend.service.jwt

import com.soongan.soonganbackend.enums.TokenType
import com.soongan.soonganbackend.persistence.jwt.JwtData
import com.soongan.soonganbackend.persistence.jwt.JwtRepository
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtRepository: JwtRepository,
    private val env: Environment
) {

    fun issueTokens(userEmail: String, authorities: List<String>): Pair<String, String> {
        val accessToken = createToken(userEmail, authorities, TokenType.ACCESS)
        val refreshToken = createToken(userEmail, authorities, TokenType.REFRESH)

        jwtRepository.save(
            JwtData(
                userEmail = userEmail,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )

        return Pair(accessToken, refreshToken)
    }

    fun createToken(userEmail: String, authorities: List<String>, tokenType: TokenType): String {
        val claims =  Jwts.claims().subject(userEmail)  // Jwt payload에 저장되는 정보
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

    fun getSecretKey(): SecretKey {
        val secretKey = env.getProperty("jwt.secret")!!
        return Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    fun getPayload(token: String, tokenType: TokenType): Map<String, Any> {  // 토큰을 읽어 페이로드 정보를 가져오는 함수, 만약 유효하지 않다면 null
        try {
            when (tokenType) {
                TokenType.ACCESS -> {
                    jwtRepository.findByAccessToken(token)
                        ?: throw SoonganException(StatusCode.INVALID_JWT_TOKEN, "유효하지 않은 토큰입니다.")
                }
                TokenType.REFRESH -> {
                    jwtRepository.findByRefreshToken(token)
                        ?: throw SoonganException(StatusCode.INVALID_JWT_TOKEN, "유효하지 않은 토큰입니다.")
                }
            }

            val payload = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .payload

            return if (payload.expiration.after(Date())) {
                payload
            } else {
                throw SoonganException(StatusCode.INVALID_JWT_TOKEN, "만료된 토큰입니다.")
            }
        } catch (e: JwtException) {
            throw SoonganException(StatusCode.INVALID_JWT_TOKEN, "유효하지 않은 토큰입니다.")
        }
    }

    fun deleteToken(email: String) {
        jwtRepository.deleteByUserEmail(email)
    }
}
