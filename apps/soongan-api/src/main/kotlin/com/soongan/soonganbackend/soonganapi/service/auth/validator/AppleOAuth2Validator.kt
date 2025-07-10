package com.soongan.soonganbackend.soonganapi.service.auth.validator

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import com.soongan.soonganbackend.soonganapi.service.auth.validator.dto.OAuth2ValidateResult
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Service
class AppleOAuth2Validator(
    private val restTemplate: RestTemplate
) {

    fun validateTokenAndGetEmail(idToken: String): OAuth2ValidateResult {
        val applePublicKeysUrl = "https://appleid.apple.com/auth/keys"
        val applePublicKeySets = restTemplate.getForObject<Map<*, *>>(
            applePublicKeysUrl
        )["keys"] as List<*>

        val signedJWT = SignedJWT.parse(idToken)
        val header = signedJWT.header as JWSHeader
        val kid = header.keyID

        val applePublicKeySet = applePublicKeySets.find { keySet ->
            val keySetMap = keySet as Map<*, *>
            keySetMap["kid"] == kid
        }?.let {
            it as Map<*, *>
        } ?: throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Applie IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")

        val rsaKey = RSAKey.Builder(
            Base64URL(applePublicKeySet["n"] as String),
            Base64URL(applePublicKeySet["e"] as String)
        ).build()

        val verifier = RSASSAVerifier(rsaKey)
        if (!signedJWT.verify(verifier)) {
            throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Applie IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")
        }

        val claims = signedJWT.jwtClaimsSet
        return OAuth2ValidateResult(
            providerId = claims.subject,
            // 애플은 email을 오직 1회만 제공하므로, 그 때 저장을 하지 못한 경우를 대비해 기본값 설정
            email = claims.getStringClaim("email") ?: "${claims.subject}@apple.soongan.site"
        )
    }
}