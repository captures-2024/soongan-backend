package com.soongan.soonganbackend.util.common.exception

class SoonganException(
    val statusCode: StatusCode,
    val detailMessage: String = ""
): RuntimeException(statusCode.message + detailMessage) {

}
