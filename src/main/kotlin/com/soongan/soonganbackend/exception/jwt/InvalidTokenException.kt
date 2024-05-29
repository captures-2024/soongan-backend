package com.soongan.soonganbackend.exception.jwt

class InvalidTokenException(
    override val message: String,
): JwtException()