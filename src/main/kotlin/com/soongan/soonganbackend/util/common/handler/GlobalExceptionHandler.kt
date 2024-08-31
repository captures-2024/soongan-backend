package com.soongan.soonganbackend.util.common.handler

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.soongan.soonganbackend.util.common.dto.CommonErrorResponseDto
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ValidationException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.security.InvalidParameterException

@RestControllerAdvice
@ResponseBody
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger { }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleCommonException(ex: Exception): CommonErrorResponseDto {
        logger.error { ex.stackTraceToString() }
        return CommonErrorResponseDto.from(StatusCode.SERVICE_NOT_AVAILABLE)
    }

    @ExceptionHandler(SoonganException::class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleSoonganException(ex: SoonganException): CommonErrorResponseDto {
        logger.error { ex.stackTraceToString() }
        return CommonErrorResponseDto.from(ex.statusCode)
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
        HttpRequestMethodNotSupportedException::class,
        BindException::class,
        NoResourceFoundException::class
    )
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    fun handleInvalidParameterException(exception: Exception): CommonErrorResponseDto {
        val statusCode: StatusCode = StatusCode.SOONGAN_API_INVALID_REQUEST

        val errorMessage: String =
            when {
                exception is HttpMessageNotReadableException
                        && exception.rootCause is InvalidFormatException -> {
                    val fieldName = (exception.rootCause as InvalidFormatException).path[0].fieldName
                    "There is a missing parameter, detail : missing '${fieldName}'"
                }
                exception is BindException -> {
                    try {
                        val fieldNames = exception.bindingResult.allErrors.joinToString {
                            "${(it as FieldError).field}:${it.defaultMessage}"
                        }
                        "There are invalid parameters, detail : ['${fieldNames}']"
                    } catch (e: Exception) {
                        exception.message
                    }
                }
                else -> statusCode.message
            }

        logger.error { exception.stackTraceToString() }
        return CommonErrorResponseDto.from(statusCode, errorMessage)
    }
}
