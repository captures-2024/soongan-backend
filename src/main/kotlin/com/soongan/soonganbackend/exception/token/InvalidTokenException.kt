package com.soongan.soonganbackend.exception.token

class InvalidTokenException(
    override val message: String,
): JwtException(message)