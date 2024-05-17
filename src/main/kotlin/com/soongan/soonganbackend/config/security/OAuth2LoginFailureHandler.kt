package com.soongan.soonganbackend.config.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2LoginFailureHandler: AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        // OAuth2 인증 실패 시 에러 메시지를 쿼리 파라미터로 담아 리다이렉트
        response.sendRedirect("${request.scheme}://${request.serverName}:${request.serverPort}/login?error=${exception.message}")
    }
}