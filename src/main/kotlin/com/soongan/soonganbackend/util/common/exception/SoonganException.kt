package com.soongan.soonganbackend.util.common.exception

open class SoonganException(
    val statusCode: StatusCode,
    open val detailMessage: String = ""
): RuntimeException(statusCode.message + detailMessage) {

}
