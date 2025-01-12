package com.soongan.soonganbackend.soonganapi.unit.service.member

import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.request.UpdateProfileRequestDto
import com.soongan.soonganbackend.soonganapi.service.member.MemberService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.service.GcpStorageService
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MemberServiceTest {

    @MockK
    private lateinit var memberAdapter: MemberAdapter

    @MockK
    private lateinit var gcpStorageService: GcpStorageService

    @InjectMockKs
    private lateinit var memberService: MemberService

    @Test
    fun `회원 정보 조회`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
            nickname = "test-nickname",
            birthYear = 1997,
            profileImageUrl = "test-profile-image-url",
            selfIntroduction = "test-self-introduction"
        )

        // when
        val result = memberService.getMemberInfo(loginMember)

        // then
        assertThat(result)
            .extracting("email", "nickname", "birthYear", "profileImageUrl", "selfIntroduction")
            .containsExactly(
                loginMember.email,
                loginMember.nickname,
                loginMember.birthYear,
                loginMember.profileImageUrl,
                loginMember.selfIntroduction
            )
    }

    @Test
    fun `닉네임 중복 체크 성공`() {
        // given
        val nickname1 = "test-nickname"
        val nickname2 = "test-nickname2"

        // mock
        every { memberAdapter.getByNickname(nickname1) } returns null
        every { memberAdapter.getByNickname(nickname2) } returns MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
            nickname = nickname2,
        )

        // when
        val result1 = memberService.checkNickname(nickname1)
        val result2 = memberService.checkNickname(nickname2)

        // then
        assertThat(result1).isTrue
        assertThat(result2).isFalse
    }

    @Test
    fun `생년월일 수정 성공`() {
        // given
        val loginMember = MemberEntity(id = 1)
        val birthYear = 1997

        // mock
        every { memberAdapter.save(any()) } returns loginMember.copy(birthYear = birthYear)

        // when
        val result = memberService.updateBirthYear(loginMember, birthYear)

        // then
        assertThat(result.birthYear).isEqualTo(birthYear)
    }

    @Test
    fun `프로필 정보 수정 성공 - 프로필 사진 수정 X`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            nickname = "test-nickname",
            birthYear = 1997,
            profileImageUrl = "test-profile-image-url"
        )
        val request = UpdateProfileRequestDto(
            nickname = "test-nickname2",
            selfIntroduction = "test-self-introduction"
        )

        // mock
        every { memberAdapter.getByNickname(request.nickname!!) } returns null
        every { memberAdapter.save(any()) } returns loginMember.copy(
            nickname = request.nickname,
            selfIntroduction = request.selfIntroduction
        )

        // when
        val result = memberService.updateProfile(loginMember, request)

        // then
        assertThat(result)
            .extracting("nickname", "selfIntroduction", "profileImageUrl")
            .containsExactly(request.nickname, request.selfIntroduction, null)
    }

//    // TODO: 프로필 사진 수정 O 테스트 코드 작성
//    @Test
//    fun `프로필 정보 수정 성공 - 프로필 사진 수정 O`() {
//    }

    @Test
    fun `프로필 정보 수정 실패 - 중복 닉네임`() {
        // given
        val loginMember = MemberEntity(id = 1)
        val request = UpdateProfileRequestDto(
            nickname = "test-nickname"
        )

        // mock
        every { memberAdapter.getByNickname(request.nickname!!) } returns MemberEntity(id = 2)

        // when, then
        assertThatThrownBy { memberService.updateProfile(loginMember, request) }
            .isInstanceOf(SoonganException::class.java)
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_DUPLICATED_NICKNAME)
    }
}