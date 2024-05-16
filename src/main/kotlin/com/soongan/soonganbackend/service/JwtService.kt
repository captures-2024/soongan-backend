package com.soongan.soonganbackend.service

import com.soongan.soonganbackend.enums.TokenType
import com.soongan.soonganbackend.repository.JwtRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtRepository: JwtRepository
) {

    fun createToken(userEmail: String, authorities: List<String>, tokenType: TokenType): String {
        val claims =  Jwts.claims().subject(userEmail)  // Jwt payload에 저장되는 정보
            .add("authorities", authorities)  // 유저의 권한 정보도 저장
            .build()

        val issuedAt = Date()
        val expiration = when (tokenType) {
            TokenType.ACCESS -> Date(issuedAt.time + 1000 * 60 * 30)  // 30분
            TokenType.REFRESH -> Date(issuedAt.time + 1000 * 60 * 60 * 24 * 14)  // 14일
        }

        return Jwts.builder()
            .claims(claims)
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(getSecretKey())
            .compact()
    }

    fun getSecretKey(): SecretKey {  // Jwt 암호화 키를 가져오는 메서드, 현재는 임시 키 "secret"을 사용
        val secret = Base64.getEncoder().encodeToString("secret".toByteArray())
        return Keys.hmacShaKeyFor(secret.toByteArray())
    }
}