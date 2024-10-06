package com.soongan.soonganbackend.soongansupport.util.exception

class SoonganUnauthorizedException (
    override val statusCode: StatusCode
): SoonganException(StatusCode.UNAUTHORIZED)
