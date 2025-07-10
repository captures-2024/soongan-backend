package com.soongan.soonganbackend.soonganapi.service.auth.validator.dto

data class OAuth2ValidateResult(
    val providerId: String,
    val email: String,
)
