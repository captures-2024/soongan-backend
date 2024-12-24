package com.soongan.soonganbackend.soonganapi.service.auth

import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request.LoginRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request.RefreshRequestDto
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
import com.soongan.soonganbackend.soonganweb.resolver.JwtHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class AuthServiceTest {
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var fcmTokenAdapter: FcmTokenAdapter
    private lateinit var jwtHandler: JwtHandler
    private lateinit var googleOAuth2Validator: GoogleOAuth2Validator
    private lateinit var kakaoOAuth2Validator: KakaoOAuth2Validator
    private lateinit var appleOAuth2Validator: AppleOAuth2Validator
    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        memberAdapter = mockk()
        fcmTokenAdapter = mockk()
        jwtHandler = mockk()
        googleOAuth2Validator = mockk()
        kakaoOAuth2Validator = mockk()
        appleOAuth2Validator = mockk()

        authService = AuthService(
            memberAdapter,
            fcmTokenAdapter,
            jwtHandler,
            googleOAuth2Validator,
            kakaoOAuth2Validator,
            appleOAuth2Validator
        )
    }

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
        assertNotNull(result)
        assertEquals("access-token", result.accessToken)
        assertEquals("refresh-token", result.refreshToken)
        verify { memberAdapter.save(any()) }
        verify { fcmTokenAdapter.save(any()) }
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
        assertNotNull(result)
        assertEquals("access-token", result.accessToken)
        assertEquals("refresh-token", result.refreshToken)
        verify(exactly = 0) { memberAdapter.save(any()) }
        verify { fcmTokenAdapter.save(any()) }
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
        assertThrows<SoonganException> {
            authService.login(UserAgentEnum.ANDROID, loginDto)
        }
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
        assertThrows<SoonganException> {
            authService.login(UserAgentEnum.ANDROID, loginDto)
        }
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
        val member = MemberEntity(
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )

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
        val member = MemberEntity(
            email = email,
            provider = ProviderEnum.GOOGLE
        )

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
    }
}