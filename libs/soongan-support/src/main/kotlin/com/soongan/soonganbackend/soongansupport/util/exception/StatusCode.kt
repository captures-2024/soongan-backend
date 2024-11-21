package com.soongan.soonganbackend.soongansupport.util.exception

enum class StatusCode(val code: Int, val message: String) {

    // 1~600 Http Status Code
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),

    // 600 ~ 700 Token Status Code
    INVALID_JWT(600, "Invalid JWT"),
    INVALID_JWT_ACCESS_TOKEN(601, "Invalid JWT Access Token"),
    INVALID_JWT_REFRESH_TOKEN(602, "Invalid JWT Refresh Token"),
    EXPIRED_JWT(603, "Expired JWT"),
    INVALID_OAUTH2_ID_TOKEN(604, "Invalid OAuth2 ID Token"),
    MISSING_JWT(605, "Missing JWT"),

    // 700 ~ 800 Member Status Code
    NOT_FOUND_MEMBER_BY_EMAIL(700, "Not Found Member by email"),

    // 1000 Api Status Code
    SOONGAN_API_INVALID_REQUEST(1000, "Invalid Request"),
    SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST(1001, "Not Found Weekly Contest"),
    SOONGAN_API_NOT_FOUND_MEMBER(1002, "Not Found Member"),
    SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST(1003, "Not Found Weekly Contest Post"),
    SOONGAN_API_INVALID_CONTEST_TYPE(1004, "Invalid Contest Type"),
    SOONGAN_API_DUPLICATED_LIKE(1005, "Duplicated Like"),
    SOONGAN_API_FAIL_TO_LOGOUT(1006, "Fail to Logout"),
    SOONGAN_API_ALREADY_EXIST_FCM_TOKEN(1007, "Already Exist FCM Token"),
    SOONGAN_API_NOT_FOUND_FCM_TOKEN(1008, "Not Found FCM Token"),
    SOONGAN_API_FAILED_PUSH_FCM_MESSAGE(1009, "Failed Push FCM Message"),
    SOONGAN_API_WEEKLY_CONTEST_POST_REGISTER_LIMIT_EXCEEDED(1010, "Weekly Contest Post Register Limit Exceeded"),
    SOONGAN_API_NOT_OWNER_WEEKLY_CONTEST_POST(1011, "Not Owner Weekly Contest Post"),
    SOONGAN_API_NOT_FOUND_PARENT_COMMENT(1012, "Not Found Parent Comment"),
    SOONGAN_API_NOT_FOUND_COMMENT(1013, "Not Found Comment"),
    SOONGAN_API_NOT_OWNER_COMMENT(1014, "Not Owner Comment"),


    // 9000 Common Status Code
    SERVICE_NOT_AVAILABLE(9000, "Service Not Available"),
}
