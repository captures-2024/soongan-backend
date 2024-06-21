package com.soongan.soonganbackend.util.common.handler

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.soongan.soonganbackend.util.common.constant.MdcConstant
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import jakarta.validation.ValidationException
import org.slf4j.MDC
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.server.ServerWebInputException
import java.security.InvalidParameterException

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

    @ExceptionHandler(
        InvalidParameterException::class,
        ValidationException::class,
        ServerWebInputException::class,
        HttpMessageNotReadableException::class,
        MethodArgumentTypeMismatchException::class,
        MissingServletRequestParameterException::class,
        ServletRequestBindingException::class,
        TypeMismatchException::class,
        HttpMessageNotReadableException::class,
        HttpMessageNotWritableException::class,
        MissingServletRequestPartException::class,
        BindException::class
    )
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    fun handleInvalidParameterException(exception: Exception): ErrorResponseDto<StatusCode> {
        val statusCode: StatusCode = StatusCode.SOONGAN_API_INVALID_REQUEST

        val errorMessage: String =
            if (exception is HttpMessageNotReadableException
                && exception.rootCause is InvalidFormatException
            ) {
                val fieldName = (exception.rootCause as InvalidFormatException).path[0].fieldName
                "There is a missing parameter, detail : missing '${fieldName}'"
            } else if (exception is BindException) {
                val fieldNames = exception.bindingResult.allErrors.joinToString {
                    "${(it as FieldError).field}:${it.defaultMessage}"
                }
                "There are invalid parameters, detail : ['${fieldNames}']"
            } else {
                exception.message.toString()
            }

        return ErrorResponseDto(statusCode, errorMessage)
    }
}
