package com.soongan.soonganbackend.util.common.handler

import com.soongan.soonganbackend.util.common.constant.MdcConstant
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@ResponseBody
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleCommonException(ex: Exception): ErrorResponseDto<StatusCode> {
        MDC.put(MdcConstant.ERROR_STATUS_CODE, ex.message)
        return ErrorResponseDto(StatusCode.SERVICE_NOT_AVAILABLE)
    }

    @ExceptionHandler(SoonganException::class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleSoonganException(ex: SoonganException): ErrorResponseDto<StatusCode> {
        MDC.put(MdcConstant.ERROR_STATUS_CODE, ex.statusCode.code)
        return ErrorResponseDto(ex.statusCode, ex.message ?: "")
    }
}
