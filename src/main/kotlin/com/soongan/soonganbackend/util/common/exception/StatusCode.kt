package com.soongan.soonganbackend.util.common.exception

enum class StatusCode(val code: String, val message: String) {

    // 1~1000 Http Status Code
    OK("200", "OK"),
    BAD_REQUEST("400", "Bad Request"),
    UNAUTHORIZED("401", "Unauthorized"),
    FORBIDDEN("403", "Forbidden"),
    NOT_FOUND("404", "Not Found"),

    // Token Status Code
    INVALID_JWT_TOKEN("498", "Invalid JWT Token"),
    INVALID_OAUTH2_ID_TOKEN("499", "Invalid OAuth2 ID Token"),

    // 1000 Api Status Code
    INVALID_REQUEST("1000", "Not Found User"),


    // 9000 Common Status Code
    SERVICE_NOT_AVAILABLE("9000", "Service Not Available"),
}
