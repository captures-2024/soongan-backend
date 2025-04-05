package com.soongan.soonganbackend.soonganweb.filter

import com.soongan.soonganbackend.soonganredis.jwt.JwtTypeEnum
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soongansupport.util.converter.HttpMvcResponseJsonConverter
import com.soongan.soonganbackend.soongansupport.util.dto.CommonErrorResponseDto
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganUnauthorizedException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import com.soongan.soonganbackend.soonganweb.resolver.JwtHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtHandler: JwtHandler
): OncePerRequestFilter() {

    private val kLogger = KotlinLogging.logger { }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val requestUri = request.requestURI
        val requestMethod = request.method

        return when (requestMethod) {
            "GET" -> Uri.isPass(requestUri, Uri.passGetUris)
            "POST" -> Uri.isPass(requestUri, Uri.passPostUris)
            else -> false
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = request.getHeader("Authorization")?.substringAfter("Bearer ")

        if (!accessToken.isNullOrBlank()) {  // accessToken이 있는데 유효하지 않으면 에러
            try {
                val payload = jwtHandler.getPayload(accessToken, JwtTypeEnum.ACCESS)
                val email = payload["sub"] as String
                val auth = UsernamePasswordAuthenticationToken(email, null, listOf())
                SecurityContextHolder.getContext().authentication = auth
            } catch (sue: SoonganUnauthorizedException) {
                kLogger.error { "${sue.statusCode} \n ${sue.stackTraceToString()}" }
                val errorResponse = CommonErrorResponseDto.from(StatusCode.UNAUTHORIZED)
                HttpMvcResponseJsonConverter.writeJsonResponse(response, errorResponse)
                return
            }
        }

        // accessToken이 없는 경우는 그냥 계속 필터 체인을 진행하도록 허용 (나중에 @LoginMember에서 throwIfUnauthorized가 true인 경우에 처리)
        filterChain.doFilter(request, response)
    }
}
