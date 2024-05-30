package com.soongan.soonganbackend.persistence.member

import com.soongan.soonganbackend.enums.Provider
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2MemberService(
    private val memberRepository: MemberRepository
): OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {  // OAuth2 로그인을 하면 이 메서드가 호출됨
        val clientRegistration = userRequest.clientRegistration
        val oAuth2User = DefaultOAuth2UserService().loadUser(userRequest)  // OAuth2 로그인을 완료한 유저 정보를 담은 객체
        val attributes = oAuth2User.attributes  // OAuth2User의 정보를 담은 Map 객체

        val provider = when (clientRegistration.registrationId) {
            "google" -> Provider.GOOGLE
            "kakao" -> Provider.KAKAO
            "apple" -> Provider.APPLE
            else -> throw IllegalArgumentException("지원하지 않는 OAuth2 Provider입니다.")
        }

        // 만약 해당 이메일로 가입된 회원이 없다면(처음 로그인하는 유저라면) 회원 정보를 저장
        memberRepository.findByEmail(attributes["email"].toString())
            ?: memberRepository.save(MemberEntity(
                email = attributes["email"].toString(),
                nickname = null,
                birthDate = null,
                profileImageUrl = null,
                provider = provider,
                authorities = oAuth2User.authorities.joinToString(","),
                withdrawalAt = null
            ))

        return oAuth2User
    }


}
