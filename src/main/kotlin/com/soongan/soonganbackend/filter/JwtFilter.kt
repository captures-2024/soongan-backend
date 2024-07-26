package com.soongan.soonganbackend.filter

import com.soongan.soonganbackend.enums.TokenType
import com.soongan.soonganbackend.service.jwt.JwtService
import com.soongan.soonganbackend.persistence.member.MemberRepository
import com.soongan.soonganbackend.util.common.constant.Uri
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtService: JwtService,
    private val memberRepository: MemberRepository
): OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val requestUri = request.requestURI

        return Uri.passUris.any { passUri ->
            if (passUri.endsWith("/**")) {
                requestUri.startsWith(passUri.removeSuffix("/**"))
            } else {
                requestUri == passUri
            }
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = request.getHeader("Authorization")?.substringAfter("Bearer ")
            ?: throw SoonganException(StatusCode.FORBIDDEN, "요청에 JWT가 존재하지 않습니다.")

        val payload = jwtService.getPayload(accessToken, TokenType.ACCESS)

        val email = payload["sub"] as String
        val member = memberRepository.findByEmail(email)
            ?: throw SoonganException(StatusCode.FORBIDDEN, "유효하지 않은 토큰입니다.")

        val memberDetail = member.toMemberDetails()

        val auth = UsernamePasswordAuthenticationToken(memberDetail, null, memberDetail.memberAuthorities)
        SecurityContextHolder.getContext().authentication = auth
        filterChain.doFilter(request, response)
    }
}
