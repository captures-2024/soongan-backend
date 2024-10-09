package com.soongan.soonganbackend.soonganredis.jwt

import org.springframework.data.repository.CrudRepository

interface JwtRepository: CrudRepository<JwtData, String> {

    fun findByAccessToken(accessToken: String): JwtData?  // AccessToken을 받아 JwtData에 대한 정보를 가져오기 위한 메소드
    fun findByRefreshToken(refreshToken: String): JwtData?  // 나중에 RefreshToken을 받아 JwtData에 대한 정보를 가져오기 위한 메소드

    fun deleteByUserEmail(userEmail: String)  // userEmail을 받아 JwtData에 대한 정보를 삭제하기 위한 메소드
}
