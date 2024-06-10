package com.soongan.soonganbackend.enums

enum class TokenType(
    val type: String
) {
    ACCESS("access"),
    REFRESH("refresh")
}