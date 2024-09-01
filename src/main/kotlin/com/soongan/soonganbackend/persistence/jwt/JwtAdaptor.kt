package com.soongan.soonganbackend.persistence.jwt

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class JwtAdaptor(
    private val jwtRepository: JwtRepository
) {

    @Transactional
    fun save(jwt: JwtData): JwtData {
        return jwtRepository.save(jwt)
    }

    @Transactional(readOnly = true)
    fun findByAccessToken(accessToken: String): JwtData? {
        return jwtRepository.findByAccessToken(accessToken)
    }

    @Transactional(readOnly = true)
    fun findByRefreshToken(refreshToken: String): JwtData? {
        return jwtRepository.findByRefreshToken(refreshToken)
    }

    @Transactional
    fun deleteByUserEmail(userEmail: String) {
        jwtRepository.deleteByUserEmail(userEmail)
    }
}
