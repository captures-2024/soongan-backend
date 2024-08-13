package com.soongan.soonganbackend.util.common.exception

open class SoonganException(
    open val statusCode: StatusCode,
    detailMessage: String = ""
): RuntimeException("${statusCode.message} ${detailMessage}")
