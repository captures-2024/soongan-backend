package com.soongan.soonganbackend.soonganweb.filter

import com.soongan.soonganbackend.soongansupport.util.converter.HttpMvcResponseJsonConverter
import com.soongan.soonganbackend.soongansupport.util.dto.CommonErrorResponseDto
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyFilter(
    @Value("\${soongan.api-key}") private val validApiKey: String
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val apiKey = request.getHeader("Soongan-Api-Key")

        if (apiKey != null && apiKey == validApiKey) {
            filterChain.doFilter(request, response)
        } else {
            val errorResponse = CommonErrorResponseDto.from(StatusCode.FORBIDDEN)
            HttpMvcResponseJsonConverter.writeJsonResponse(response, errorResponse)
            return
        }
    }
}