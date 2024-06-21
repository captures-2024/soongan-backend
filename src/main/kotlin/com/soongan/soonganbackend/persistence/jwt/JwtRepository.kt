package com.soongan.soonganbackend.persistence.jwt

import org.springframework.data.repository.CrudRepository

interface JwtRepository: CrudRepository<JwtData, String> {

    fun findByRefreshToken(refreshToken: String): JwtData?  // 나중에 RefreshToken을 받아 JwtData에 대한 정보를 가져오기 위한 메소드
}
