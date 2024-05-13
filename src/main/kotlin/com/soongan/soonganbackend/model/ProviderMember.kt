package com.soongan.soonganbackend.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User

class ProviderMember(
    private val oAuth2User: OAuth2User,
    private val clientRegistration: ClientRegistration
) {
    private val attributes: Map<String, Any>
        get() = oAuth2User.attributes

    val id: String
        get() = attributes["sub"].toString()
    val email: String
        get() = attributes["email"].toString()
    val provider: String
        get() = clientRegistration.registrationId
    val authorities: List<GrantedAuthority>
        get() = oAuth2User.authorities.stream().map {
            SimpleGrantedAuthority(it.authority)
        }.toList()
}