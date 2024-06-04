package com.soongan.soonganbackend.util.common.exception

enum class StatusCode(val code: String, val message: String) {

    // 1~1000 Http Status Code
    OK("200", "OK"),
    BAD_REQUEST("400", "Bad Request"),
    UNAUTHORIZED("401", "Unauthorized"),

    // 1000 Api Status Code
    NOT_FOUND_USER("1000", "Not Found User"),


    // 9000 Common Status Code
    SERVICE_NOT_AVAILABLE("9000", "Service Not Available"),
}
