package com.soongan.soonganbackend.util.common.handler

import com.soongan.soonganbackend.util.common.dto.CommonErrorResponseDto
import com.soongan.soonganbackend.util.common.dto.ExportResponseDto
import com.soongan.soonganbackend.util.common.exception.StatusCode
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
        return when (body) {
            is CommonErrorResponseDto -> {
                body
            }

            else -> ExportResponseDto.from(StatusCode.OK, body)
        }
    }
}
