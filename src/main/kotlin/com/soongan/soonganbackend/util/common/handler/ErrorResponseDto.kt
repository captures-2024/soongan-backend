package com.soongan.soonganbackend.util.common.handler

import com.soongan.soonganbackend.util.common.exception.StatusCode

data class ErrorResponseDto<T>(

    val statusCode: String = "",

    var errorMessage: String,

    var responseData: T? = null
) {

    constructor(statusCode: StatusCode) : this(
        statusCode.code,
        statusCode.message
    )

    constructor(statusCode: StatusCode, errorMessage: String) : this(
        statusCode.code,
        errorMessage
    )

    constructor(statusCode: StatusCode, responseData: T? = null) : this(
        statusCode.code,
        statusCode.message,
        responseData
    )
}
