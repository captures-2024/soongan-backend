package com.soongan.soonganbackend.soonganapi.service.auth.validator

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import com.soongan.soonganbackend.soonganapi.service.auth.validator.dto.OAuth2ValidateResult
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Service
class AppleOAuth2Validator(
    private val restTemplate: RestTemplate
) {
    private val logger = KotlinLogging.logger {}

    fun validateTokenAndGetEmail(idToken: String): OAuth2ValidateResult {
        val startTime = System.currentTimeMillis()
        val applePublicKeysUrl = "https://appleid.apple.com/auth/keys"

        try {
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
            }

            if (applePublicKeySet == null) {
                val duration = System.currentTimeMillis() - startTime
                logger.warn { "[OAuth] apple_validate_failed provider=APPLE, duration=${duration}ms, reason=kid not found in public keys" }
                throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Apple IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")
            }

            val rsaKey = RSAKey.Builder(
                Base64URL(applePublicKeySet["n"] as String),
                Base64URL(applePublicKeySet["e"] as String)
            ).build()

            val verifier = RSASSAVerifier(rsaKey)
            if (!signedJWT.verify(verifier)) {
                val duration = System.currentTimeMillis() - startTime
                logger.warn { "[OAuth] apple_validate_failed provider=APPLE, duration=${duration}ms, reason=signature verification failed" }
                throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Apple IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")
            }

            val claims = signedJWT.jwtClaimsSet
            return OAuth2ValidateResult(
                providerId = claims.subject,
                // 애플은 email을 오직 1회만 제공하므로, 그 때 저장을 하지 못한 경우를 대비해 기본값 설정
                email = claims.getStringClaim("email") ?: "${claims.subject}@apple.soongan.site"
            )
        } catch (e: SoonganException) {
            throw e
        } catch (e: RestClientException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error(e) { "[OAuth] apple_api_failed provider=APPLE, duration=${duration}ms, reason=failed to fetch public keys" }
            throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Apple 공개키 조회 중 오류가 발생했습니다: ${e.message}")
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error(e) { "[OAuth] apple_validate_error provider=APPLE, duration=${duration}ms, reason=token parsing or verification error" }
            throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Apple IdToken 검증 중 오류가 발생했습니다: ${e.message}")
        }
    }
}