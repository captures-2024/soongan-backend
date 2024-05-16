package com.soongan.soonganbackend.service

import com.soongan.soonganbackend.enums.TokenType
import com.soongan.soonganbackend.model.JwtData
import com.soongan.soonganbackend.repository.JwtRepository
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtRepository: JwtRepository
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

    fun getSecretKey(): SecretKey {  // Jwt 암호화 키를 가져오는 메서드, 현재는 임시 키 사용
        val secret = Base64.getEncoder().encodeToString("secret123456789123456789".toByteArray())
        return Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun getPayload(token: String): Map<String, Any>? {  // 토큰을 읽어 페이로드 정보를 가져오는 함수, 만약 유효하지 않다면 null
        try {
            val payload = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .payload

            return if (payload.expiration.after(Date())) {
                payload
            } else {
                null
            }
        } catch (e: JwtException) {
            return null
        }
    }
}