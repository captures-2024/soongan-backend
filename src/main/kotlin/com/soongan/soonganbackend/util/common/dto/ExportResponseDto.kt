package com.soongan.soonganbackend.util.common.dto

import com.soongan.soonganbackend.util.common.exception.StatusCode

class ExportResponseDto(
    override val statusCode: String,
    override val message: String,
    var responseData: Any? = null
): CommonErrorResponseDto(statusCode, message){

    companion object {
        fun from(
            statusCode: StatusCode,
            responseData: Any? = null
        ): ExportResponseDto =
            ExportResponseDto(
                statusCode = statusCode.code,
                message = statusCode.message,
                responseData = responseData
        )
    }
}
