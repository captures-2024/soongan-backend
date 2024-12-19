package com.soongan.soonganbackend.soongansupport.util.exception

open class SoonganException(
    open val statusCode: StatusCode,
    val detailMessage: String = ""
): RuntimeException("${statusCode.message}: ${detailMessage}")
