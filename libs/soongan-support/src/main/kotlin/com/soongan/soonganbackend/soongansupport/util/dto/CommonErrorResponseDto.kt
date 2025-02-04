package com.soongan.soonganbackend.soongansupport.util.dto

import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode

open class CommonErrorResponseDto(
    open val statusCode: Int,
    open val message: String,
    open val detailMessage: String? = null
) {
    companion object {
        fun from(
            statusCode: StatusCode,
            errorMessage: String? = null,
            detailMessage: String? = null
        ): CommonErrorResponseDto =
            CommonErrorResponseDto(
                statusCode = statusCode.code,
                message = errorMessage ?: statusCode.message,
                detailMessage = detailMessage
            )
    }
}
