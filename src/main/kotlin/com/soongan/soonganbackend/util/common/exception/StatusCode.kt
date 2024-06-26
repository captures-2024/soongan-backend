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
    SOONGAN_API_INVALID_REQUEST("1000", "Invalid Request"),
    SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST("1001", "Not Found Weekly Contest"),
    SOONGAN_API_NOT_FOUND_MEMBER("1002", "Not Found Member"),
    SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST("1003", "Not Found Weekly Contest Post"),
    SOONGAN_API_INVALID_CONTEST_TYPE("1004", "Invalid Contest Type"),
    SOONGAN_API_DUPLICATED_LIKE("1005", "Duplicated Like"),



    // 9000 Common Status Code
    SERVICE_NOT_AVAILABLE("9000", "Service Not Available"),
}
