package com.soongan.soonganbackend.soongansupport.util.dto

import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode

class ExportResponseDto(
    override val statusCode: Int,
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
