package com.soongan.soonganbackend.util.common.constant

object Uri {
    const val API = "/api"
    const val V3 = "/v3"
    const val DOCS = "/docs"
    const val API_DOCS = "/api-docs"
    const val SWAGGER_UI = "/swagger-ui"
    const val SWAGGER_RESOURCES = "/swagger-resources"

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

    val passUris = listOf(
        DOCS,
        SWAGGER_UI + "/**",
        SWAGGER_RESOURCES + "/**",
        V3 + API_DOCS + "/**",

        MEMBERS + LOGIN,
        MEMBERS + REFRESH,

        WEEKLY + CONTESTS + POSTS,

        FCM,
    )
}
