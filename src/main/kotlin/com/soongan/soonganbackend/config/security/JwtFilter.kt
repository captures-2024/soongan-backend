package com.soongan.soonganbackend.config.security

import com.soongan.soonganbackend.repository.MemberRepository
import com.soongan.soonganbackend.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtService: JwtService,
    private val memberRepository: MemberRepository
): OncePerRequestFilter() {  // Security 인증 과정 중간에 동작하는 필터 역할을 하기 위해 OncePerRequestFilter 상속

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = request.getHeader("Authorization")?.substringAfter("Bearer ")
        if (accessToken == null) {  // 만약 헤더에 토큰이 없다면 다음 필터로 이동
            doFilter(request, response, filterChain)
            return
        }

        val payload = jwtService.getPayload(accessToken)
            ?: throw Exception("유효하지 않은 토큰입니다.") // 토큰을 통해 페이로드 정보 가져오기, 만약 토큰이 유효하지 않다면 예외 발생

        val email = payload["sub"] as String  // 페이로드에서 이메일 정보 가져오기
        val member = memberRepository.findByEmail(email)
            ?: throw Exception("존재하지 않는 회원입니다.")  // 회원 정보 가져오기, 만약 회원이 존재하지 않는다면 예외 발생
        val auth = UsernamePasswordAuthenticationToken(email, null, member.authorities.map {
            SimpleGrantedAuthority(it)
        })  // Security 인증 객체 생성
        SecurityContextHolder.getContext().authentication = auth  // Security Context에 인증 객체 저장
        filterChain.doFilter(request, response)  // 다음 필터로 이동
    }
}