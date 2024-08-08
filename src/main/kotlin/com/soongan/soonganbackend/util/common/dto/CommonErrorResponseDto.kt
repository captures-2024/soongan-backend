package com.soongan.soonganbackend.util.common.dto

import com.soongan.soonganbackend.util.common.exception.StatusCode

open class CommonErrorResponseDto(
    open val statusCode: String,
    open val errorMessage: String,
) {
    companion object {
        fun from(
            statusCode: StatusCode,
            errorMessage: String? = null
        ): CommonErrorResponseDto =
            CommonErrorResponseDto(
                statusCode = statusCode.code,
                errorMessage = errorMessage ?: statusCode.message
            )
    }
}
