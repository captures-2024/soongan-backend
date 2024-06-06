package com.soongan.soonganbackend.exception.token

import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode

class InvalidOAuth2IdTokenException(
    override val detailMessage: String
): SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, detailMessage)