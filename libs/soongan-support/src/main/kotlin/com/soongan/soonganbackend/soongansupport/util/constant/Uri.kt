package com.soongan.soonganbackend.soongansupport.util.constant

object Uri {
    const val V3 = "/v3"
    const val API = "/api"
    const val API_DOCS = "/api-docs"
    const val SWAGGER_UI = "/swagger-ui"
    const val SWAGGER_RESOURCES = "/swagger-resources"
    const val SWAGGER_CONFIG = "/swagger-config"

    const val AUTH = "/auth"
    const val LOGIN = "/login"
    const val LOGOUT = "/logout"
    const val WITHDRAW = "/withdraw"
    const val REFRESH = "/refresh"

    const val MEMBERS = "/members"
    const val CHECK_NICKNAME = "/check-nickname"
    const val PROFILE = "/profile"
    const val BIRTH_YEAR = "/birth-year"

    const val HOME = "/home"

    const val WEEKLY = "/weekly"
    const val CONTESTS = "/contests"
    const val POSTS = "/posts"
    const val COMMENTS = "/comments"
    const val REPLIES = "/replies"
    const val LIKE = "/like"
    const val MY_HISTORY = "/my-hisotry"

    const val FCM = "/fcm"

    const val REPORT = "/report"

    const val CALLBACK = "/callback"
    const val APPLE_LOGIN = "/apple_login"
    const val SUCCESS = "/success"

    val passUris = listOf(
        "/_health",
        API_DOCS,
        SWAGGER_UI + "/**",
        SWAGGER_RESOURCES + "/**",
        V3 + API_DOCS + "/**",

        AUTH + LOGIN,
        AUTH + REFRESH,

        WEEKLY + CONTESTS + POSTS,

        FCM,
        FCM + "/test",

        CALLBACK + APPLE_LOGIN,
        CALLBACK + APPLE_LOGIN + SUCCESS
    )

    val notWrapUris = listOf(
        "/_health",
        V3 + API_DOCS,
        V3 + API_DOCS + SWAGGER_CONFIG,

        CALLBACK + APPLE_LOGIN,
        CALLBACK + APPLE_LOGIN + SUCCESS
    )
}
