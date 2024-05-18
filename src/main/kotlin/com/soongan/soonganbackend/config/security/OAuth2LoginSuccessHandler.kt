package com.soongan.soonganbackend.config.security

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
        // 근데 소셜 로그인을 신청한 클라이언트가 iOS인지, Android인지, Web인지에 따라 리다이렉트 URL이 달라져야 할 것 같음
        // 아래처럼 하니 그냥 localhost:8080/login으로 리다이렉트 됨..
        // 아마 백엔드에서 소셜 로그인 페이지로 리다이렉트하기 때문에 request의 정보가 백엔드 서버의 정보로만 되어있는 것 같음
        // iOS에서 요청했는지, android에서 요청했는지를 어떻게 알 방법이 필요할 것 같음
        // 그래서 각 요청에 따라 다른 URL로 리다이렉트 시키는 방법을 찾아야 할 것 같음
        // 그러나 첫 요청이 iOS나 android라고 하더라도 소셜 로그인을 진행하는 것은 백엔드라 http 요청 정보를 어떻게 유지해야할지,,
        val redirectUrl = "${request.scheme}://${request.serverName}:${request.serverPort}/login?accessToken=${issuedTokens.first}&refreshToken=${issuedTokens.second}"
        println(redirectUrl)
        response.status = HttpServletResponse.SC_OK
        response.sendRedirect(redirectUrl)
    }
}