package com.soongan.soonganbackend.soonganweb.resolver

import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soongansupport.util.dto.CommonErrorResponseDto
import com.soongan.soonganbackend.soongansupport.util.dto.ExportResponseDto
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class ApiResponseBodyWrapper: ResponseBodyAdvice<Any> {

    // 모든 api 응답에 대하여 동작하도록 설정
    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean = true

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        val requestPath = request.uri.path
        if (Uri.notWrapUris.map { Uri.API + it }.contains(requestPath)) {
            return body
        }

        return when (body) {
            is CommonErrorResponseDto -> {
                body
            }

            else -> ExportResponseDto.from(StatusCode.OK, body)
        }
    }
}
