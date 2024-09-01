package com.soongan.soonganbackend.util.common.constant

object Uri {
    const val V3 = "/v3"
    const val API_DOCS = "/api-docs"
    const val SWAGGER_UI = "/swagger-ui"
    const val SWAGGER_RESOURCES = "/swagger-resources"
    const val SWAGGER_CONFIG = "/swagger-config"

    const val MEMBERS = "/members"
    const val LOGIN = "/login"
    const val LOGOUT = "/logout"
    const val WITHDRAW = "/withdraw"
    const val CHECK_NICKNAME = "/check-nickname"
    const val NICKNAME = "/nickname"
    const val PROFILE_IMAGE = "/profile-image"
    const val REFRESH = "/refresh"

    const val WEEKLY = "/weekly"
    const val CONTESTS = "/contests"
    const val POSTS = "/posts"
    const val LIKE = "/like"

    const val FCM = "/fcm"

    const val CALLBACK = "/callback"
    const val APPLE_LOGIN = "/apple_login"
    const val SUCCESS = "/success"

    val passUris = listOf(
        API_DOCS,
        SWAGGER_UI + "/**",
        SWAGGER_RESOURCES + "/**",
        V3 + API_DOCS + "/**",

        MEMBERS + LOGIN,
        MEMBERS + REFRESH,

        WEEKLY + CONTESTS + POSTS,

        FCM,

        CALLBACK + APPLE_LOGIN,
        CALLBACK + APPLE_LOGIN + SUCCESS
    )

    val notWrapUris = listOf(
        V3 + API_DOCS,
        V3 + API_DOCS + SWAGGER_CONFIG,

        CALLBACK + APPLE_LOGIN + SUCCESS
    )
}
