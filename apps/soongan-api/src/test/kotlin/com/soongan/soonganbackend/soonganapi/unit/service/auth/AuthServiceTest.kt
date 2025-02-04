package com.soongan.soonganbackend.soonganapi.unit.service.auth

import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request.LoginRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request.RefreshRequestDto
import com.soongan.soonganbackend.soonganapi.service.auth.AuthService
import com.soongan.soonganbackend.soonganapi.service.auth.validator.AppleOAuth2Validator
import com.soongan.soonganbackend.soonganapi.service.auth.validator.GoogleOAuth2Validator
import com.soongan.soonganbackend.soonganapi.service.auth.validator.KakaoOAuth2Validator
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenEntity
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import com.soongan.soonganbackend.soonganweb.resolver.JwtHandler
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class AuthServiceTest {

    @MockK
    private lateinit var memberAdapter: MemberAdapter

    @MockK
    private lateinit var fcmTokenAdapter: FcmTokenAdapter

    @MockK
    private lateinit var jwtHandler: JwtHandler

    @MockK
    private lateinit var googleOAuth2Validator: GoogleOAuth2Validator

    @MockK
    private lateinit var kakaoOAuth2Validator: KakaoOAuth2Validator

    @MockK
    private lateinit var appleOAuth2Validator: AppleOAuth2Validator

    @InjectMockKs
    private lateinit var authService: AuthService

    @Test
    fun `로그인 성공 - 신규 회원`() {
        // given
        val email = "test@example.com"
        val loginDto = LoginRequestDto(
            provider = ProviderEnum.GOOGLE,
            idToken = "dummy-token",
            fcmToken = "fcm-token"
        )
        val member = MemberEntity(
            email = email,
            provider = ProviderEnum.GOOGLE
        )
        val fcmToken = FcmTokenEntity(
            token = "fcm-token",
            member = null,
            deviceType = UserAgentEnum.ANDROID,
            deviceId = "device-id"
        )

        // mock
        every { googleOAuth2Validator.validateTokenAndGetEmail(any(), any()) } returns email
        every { memberAdapter.getByEmail(email) } returns null
        every { memberAdapter.save(any()) } returns member
        every { fcmTokenAdapter.findByToken(loginDto.fcmToken) } returns fcmToken
        every { fcmTokenAdapter.save(any()) } returns fcmToken
        every { jwtHandler.issueTokens(email) } returns Pair("access-token", "refresh-token")

        // when
        val result = authService.login(UserAgentEnum.ANDROID, loginDto)

        // then
        verify { memberAdapter.save(any()) }
        verify { fcmTokenAdapter.save(any()) }
        assertThat(result)
            .isNotNull()
            .extracting("accessToken", "refreshToken")
            .containsExactly("access-token", "refresh-token")
    }

    @Test
    fun `로그인 성공 - 기존 회원`() {
        // given
        val email = "test@example.com"
        val loginDto = LoginRequestDto(
            provider = ProviderEnum.GOOGLE,
            idToken = "dummy-token",
            fcmToken = "fcm-token"
        )
        val member = MemberEntity(
            email = email,
            provider = ProviderEnum.GOOGLE
        )
        val fcmToken = FcmTokenEntity(
            token = "fcm-token",
            member = null,
            deviceType = UserAgentEnum.ANDROID,
            deviceId = "device-id"
        )

        // mock
        every { googleOAuth2Validator.validateTokenAndGetEmail(any(), any()) } returns email
        every { memberAdapter.getByEmail(email) } returns member
        every { fcmTokenAdapter.findByToken(loginDto.fcmToken) } returns fcmToken
        every { fcmTokenAdapter.save(any()) } returns fcmToken
        every { jwtHandler.issueTokens(email) } returns Pair("access-token", "refresh-token")

        // when
        val result = authService.login(UserAgentEnum.ANDROID, loginDto)

        // then
        verify(exactly = 0) { memberAdapter.save(any()) }
        verify { fcmTokenAdapter.save(any()) }
        assertThat(result)
            .isNotNull()
            .extracting("accessToken", "refreshToken")
            .containsExactly("access-token", "refresh-token")
    }

    @Test
    fun `로그인 실패 - 정지된 회원`() {
        // given
        val email = "test@example.com"
        val loginDto = LoginRequestDto(
            provider = ProviderEnum.GOOGLE,
            idToken = "dummy-token",
            fcmToken = "fcm-token"
        )
        val member = MemberEntity(
            email = email,
            provider = ProviderEnum.GOOGLE,
            banUntil = LocalDateTime.now().plusDays(1)
        )

        // mock
        every { googleOAuth2Validator.validateTokenAndGetEmail(any(), any()) } returns email
        every { memberAdapter.getByEmail(email) } returns member

        // when & then
        assertThatThrownBy { authService.login(UserAgentEnum.ANDROID, loginDto) }
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_BANNED_MEMBER)
    }

    @Test
    fun `로그인 실패 - FCM 토큰 없음`() {
        // given
        val email = "test@example.com"
        val loginDto = LoginRequestDto(
            provider = ProviderEnum.GOOGLE,
            idToken = "dummy-token",
            fcmToken = "fcm-token"
        )
        val member = MemberEntity(
            email = email,
            provider = ProviderEnum.GOOGLE
        )

        // mock
        every { googleOAuth2Validator.validateTokenAndGetEmail(any(), any()) } returns email
        every { memberAdapter.getByEmail(email) } returns member
        every { fcmTokenAdapter.findByToken(loginDto.fcmToken) } returns null

        // when & then
        assertThatThrownBy { authService.login(UserAgentEnum.ANDROID, loginDto) }
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_NOT_FOUND_FCM_TOKEN)
    }

    @Test
    fun `로그아웃 성공`() {
        // given
        val email = "test@example.com"

        // mock
        every { jwtHandler.deleteToken(email) } returns Unit

        // when
        authService.logout(email)

        // then
        verify { jwtHandler.deleteToken(email) }
    }

    @Test
    fun `회원탈퇴 성공`() {
        // given
        val member = MemberEntity()
        // mock
        every { memberAdapter.save(any()) } returns member.copy(withdrawalAt = LocalDateTime.now())
        every { jwtHandler.deleteToken(member.email) } returns Unit

        // when
        authService.withdraw(member)

        // then
        verify { memberAdapter.save(any()) }
        verify { jwtHandler.deleteToken(member.email) }
    }

    @Test
    fun `토큰 갱신 성공`() {
        // given
        val email = "test@example.com"
        val refreshRequestDto = RefreshRequestDto(
            accessToken = "old-access-token",
            refreshToken = "old-refresh-token"
        )
        val member = MemberEntity(email = email)

        // mock
        every { jwtHandler.validateRefreshRequest(any(), any()) } returns mapOf("sub" to email)
        every { memberAdapter.getByEmail(email) } returns member
        every { jwtHandler.issueTokens(email) } returns Pair("new-access-token", "new-refresh-token")

        // when
        val result = authService.refresh(refreshRequestDto)

        // then
        assertNotNull(result)
        assertEquals("new-access-token", result.accessToken)
        assertEquals("new-refresh-token", result.refreshToken)
        assertThat(result)
            .isNotNull()
            .extracting("accessToken", "refreshToken")
            .containsExactly("new-access-token", "new-refresh-token")
    }

    @Test
    fun `토큰 갱신 실패 - 회원 없음`() {
        // given
        val email = "test@example.com"
        val refreshRequestDto = RefreshRequestDto(
            accessToken = "old-access-token",
            refreshToken = "old-refresh-token"
        )

        // mock
        every { jwtHandler.validateRefreshRequest(any(), any()) } returns mapOf("sub" to email)
        every { memberAdapter.getByEmail(email) } returns null

        // when & then
        assertThatThrownBy { authService.refresh(refreshRequestDto) }
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_MEMBER_NOT_FOUND_MEMBER_BY_EMAIL)
    }

    @Test
    fun `토큰 갱신 실패 - 정지된 회원`() {
        // given
        val email = "test@example.com"
        val refreshRequestDto = RefreshRequestDto(
            accessToken = "old-access-token",
            refreshToken = "old-refresh-token"
        )
        val member = MemberEntity(
            email = email,
            banUntil = LocalDateTime.now().plusDays(1)
        )

        // mock
        every { jwtHandler.validateRefreshRequest(any(), any()) } returns mapOf("sub" to email)
        every { memberAdapter.getByEmail(email) } returns member

        // when & then
        assertThatThrownBy { authService.refresh(refreshRequestDto) }
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_BANNED_MEMBER)
    }

    @Test
    fun `토큰 갱신 실패 - 탈퇴한 회원`() {
        // given
        val email = "test@example.com"
        val refreshRequestDto = RefreshRequestDto(
            accessToken = "old-access-token",
            refreshToken = "old-refresh-token"
        )
        val member = MemberEntity(
            email = email,
            withdrawalAt = LocalDateTime.now()
        )

        // mock
        every { jwtHandler.validateRefreshRequest(any(), any()) } returns mapOf("sub" to email)
        every { memberAdapter.getByEmail(email) } returns member

        // when & then
        assertThatThrownBy { authService.refresh(refreshRequestDto) }
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_WITHDRAWN_MEMBER)
    }
}