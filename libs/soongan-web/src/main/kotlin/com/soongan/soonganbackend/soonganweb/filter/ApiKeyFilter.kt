package com.soongan.soonganbackend.soonganweb.filter

import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soongansupport.util.converter.HttpMvcResponseJsonConverter
import com.soongan.soonganbackend.soongansupport.util.dto.CommonErrorResponseDto
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.github.oshai.kotlinlogging.KotlinLogging
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

    private val logger = KotlinLogging.logger { }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val apiKey = request.getHeader("Soongan-Api-Key")
        val requestUri = request.requestURI
        val requestMethod = request.method

        val shouldPass = when (requestMethod) {
            "GET" -> Uri.isPass(requestUri, Uri.passGetUris)
            "POST" -> Uri.isPass(requestUri, Uri.passPostUris)
            else -> false
        }

        logger.debug("[ApiKeyFilter] URI: $requestUri, Method: $requestMethod, shouldPass: $shouldPass, hasApiKey: ${apiKey != null}")

        if (shouldPass) {
            filterChain.doFilter(request, response)
        } else if (apiKey != null && apiKey == validApiKey) {
            filterChain.doFilter(request, response)
        } else {
            logger.warn("[ApiKeyFilter] Request blocked - URI: $requestUri, Method: $requestMethod")
            val errorResponse = CommonErrorResponseDto.from(StatusCode.FORBIDDEN)
            response.status = errorResponse.statusCode
            HttpMvcResponseJsonConverter.writeJsonResponse(response, errorResponse)
            return
        }
    }
}