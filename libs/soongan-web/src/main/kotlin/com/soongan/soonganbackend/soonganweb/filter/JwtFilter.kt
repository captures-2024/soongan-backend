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
        try {
            val accessToken = request.getHeader("Authorization")?.substringAfter("Bearer ")
                ?: throw SoonganUnauthorizedException(StatusCode.MISSING_JWT)

            val payload = jwtHandler.getPayload(accessToken, JwtTypeEnum.ACCESS)
            val email = payload["sub"] as String

            val auth = UsernamePasswordAuthenticationToken(email, null, listOf())
            SecurityContextHolder.getContext().authentication = auth
            filterChain.doFilter(request, response)
        } catch (sue: SoonganUnauthorizedException) {
            kLogger.error { "${sue.statusCode} \n ${sue.stackTraceToString()}" }
            val errorResponse = CommonErrorResponseDto.from(StatusCode.UNAUTHORIZED)    // client 추상화된 에러 제공
            HttpMvcResponseJsonConverter.writeJsonResponse(response, errorResponse)
        }

    }
}
