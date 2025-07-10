package com.soongan.soonganbackend.soongansupport.util.constant

import org.springframework.util.AntPathMatcher

object Uri {
    const val HEALTH = "/_health"
    const val V3 = "/v3"
    const val API = "/api"
    const val API_DOCS = "/api-docs"
    const val SWAGGER_UI = "/swagger-ui"
    const val SWAGGER_RESOURCES = "/swagger-resources"
    const val SWAGGER_CONFIG = "/swagger-config"

    const val ADMIN = "/admin"

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
    const val HISTORIES = "/histories"
    const val POSTS = "/posts"
    const val COMMENTS = "/comments"
    const val REPLIES = "/replies"
    const val LIKE = "/like"
    const val MY_HISTORY = "/my-history"

    const val AWARDS = "/awards"

    const val FCM = "/fcm"

    const val NOTIFICATIONS = "/notifications"
    const val UNREAD_COUNT = "/unread-count"
    const val SETTINGS = "/settings"

    const val REPORT = "/report"
    const val EXPLAIN ="/explain"

    const val CALLBACK = "/callback"
    const val APPLE_LOGIN = "/apple_login"
    const val SUCCESS = "/success"

    const val CURSOR = "/cursor"

    val passGetUris = listOf(
        HEALTH,
        API_DOCS,
        SWAGGER_UI + "/**",
        SWAGGER_RESOURCES + "/**",
        V3 + API_DOCS + "/**",

        HOME,

        WEEKLY + CONTESTS,
        WEEKLY + CONTESTS + POSTS,

        AWARDS,
        AWARDS + "/{contestId:[0-9]+}",

        CALLBACK + APPLE_LOGIN,
        CALLBACK + APPLE_LOGIN + SUCCESS
    )
    val passPostUris = listOf(
        AUTH + LOGIN,
        AUTH + REFRESH,

        FCM,
        FCM + "/test"
    )

    val notWrapUris = listOf(
        HEALTH,
        V3 + API_DOCS,
        V3 + API_DOCS + SWAGGER_CONFIG,

        CALLBACK + APPLE_LOGIN,
        CALLBACK + APPLE_LOGIN + SUCCESS
    )

    fun isPass(uri: String, passUris: List<String>): Boolean {
        val matcher = AntPathMatcher()
        return passUris.any { pattern -> matcher.match(pattern, uri) }
    }
}
