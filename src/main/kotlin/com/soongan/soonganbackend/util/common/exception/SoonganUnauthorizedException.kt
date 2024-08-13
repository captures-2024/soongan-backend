package com.soongan.soonganbackend.util.common.exception

class SoonganUnauthorizedException (
    override val statusCode: StatusCode
): SoonganException(StatusCode.UNAUTHORIZED)
