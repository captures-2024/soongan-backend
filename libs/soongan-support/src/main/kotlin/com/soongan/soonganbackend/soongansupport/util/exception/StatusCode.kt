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

    // 700 ~ 799 Member Auth Status Code
    SOONGAN_MEMBER_NOT_FOUND_MEMBER_BY_EMAIL(700, "Not Found Member by email"),
    SOONGAN_MEMBER_API_FAIL_TO_LOGOUT(701, "Fail to Logout"),
    SOONGAN_API_BANNED_MEMBER(702, "Banned Member"),
    SOONGAN_API_WITHDRAWN_MEMBER(703, "Withdrawn Member"),

    // 800 ~ 899 FCM Status Code
    SOONGAN_API_ALREADY_EXIST_FCM_TOKEN(800, "Already Exist FCM Token"),
    SOONGAN_API_NOT_FOUND_FCM_TOKEN(801, "Not Found FCM Token"),
    SOONGAN_API_FAILED_PUSH_FCM_MESSAGE(802, "Failed Push FCM Message"),

    /**
     * 1001~ Api Status Code
     * Service 도메인 별로 100 단위로 코드를 부여한다.
     */
    SOONGAN_API_INVALID_REQUEST(1000, "Invalid Request"),

    // 1100 ~ 1199 Weekly Contest Status Code
    SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST(1100, "Not Found Weekly Contest"),
    SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST(1101, "Not Found Weekly Contest Post"),
    SOONGAN_API_INVALID_CONTEST_TYPE(1102, "Invalid Contest Type"),
    SOONGAN_API_WEEKLY_CONTEST_POST_REGISTER_LIMIT_EXCEEDED(1103, "Weekly Contest Post Register Limit Exceeded"),
    SOONGAN_API_NOT_OWNER_WEEKLY_CONTEST_POST(1104, "Not Owner Weekly Contest Post"),

    // 1200 ~ 1299 Like Status Code
    SOONGAN_API_DUPLICATED_LIKE(1200, "Duplicated Like"),

    // 1300 ~ 1399 Comment Status Code
    SOONGAN_API_NOT_FOUND_PARENT_COMMENT(1300, "Not Found Parent Comment"),
    SOONGAN_API_NOT_FOUND_COMMENT(1301, "Not Found Comment"),
    SOONGAN_API_NOT_OWNER_COMMENT(1302, "Not Owner Comment"),
    SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_COMMENT(1303, "Not Found Weekly Contest Comment"),

    // 1400 ~ 1499 Notification Status Code
    SOONGAN_API_NOT_FOUND_NOTIFICATION(1400, "Not Found Notification"),
    SOONGAN_API_ALREADY_READ_NOTIFICATION(1401, "Already Read Notification"),
    SOONGAN_API_CANNOT_DELETE_REPORT_CLARIFICATION(1402, "Cannot Delete Report Clarification"),



    // 9000 Common Status Code
    SERVICE_NOT_AVAILABLE(9000, "Service Not Available"),
}
