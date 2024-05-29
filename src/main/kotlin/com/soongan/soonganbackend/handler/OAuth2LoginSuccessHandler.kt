package com.soongan.soonganbackend.handler

import com.soongan.soonganbackend.service.JwtService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2LoginSuccessHandler(
    private val jwtService: JwtService
): SimpleUrlAuthenticationSuccessHandler() {

    // OAuth2 인증 성공 시 아래 메서드가 호출됨
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val oAuth2User = authentication.principal as OAuth2User  // 로그인한 유저 정보를 담은 객체
        val email = oAuth2User.attributes["email"] as String  // 이메일 정보 가져오기
        val authorities = authentication.authorities.map { it.authority }  // 권한 정보 가져오기
        val issuedTokens = jwtService.issueTokens(email, authorities)  // 이메일, 권한 정보를 통해 토큰 생성

        // 토큰을 쿼리 파라미터로 담아 리다이렉트
        val redirectUrl = "${request.scheme}://${request.serverName}:${request.serverPort}/login?accessToken=${issuedTokens.first}&refreshToken=${issuedTokens.second}"
        println(redirectUrl)
        response.status = HttpServletResponse.SC_OK
        response.sendRedirect(redirectUrl)
    }
}