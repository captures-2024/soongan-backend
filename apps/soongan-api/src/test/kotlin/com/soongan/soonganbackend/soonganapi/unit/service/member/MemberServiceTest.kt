package com.soongan.soonganbackend.soonganapi.unit.service.member

import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.request.UpdateProfileRequestDto
import com.soongan.soonganbackend.soonganapi.service.member.MemberService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.service.GcpStorageService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MemberServiceTest {
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var gcpStorageService: GcpStorageService
    private lateinit var memberService: MemberService

    @BeforeEach
    fun setUp() {
        memberAdapter = mockk()
        gcpStorageService = mockk()
        memberService = MemberService(memberAdapter, gcpStorageService)
    }

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
        assert(result.email == loginMember.email)
        assert(result.nickname == loginMember.nickname)
        assert(result.birthYear == loginMember.birthYear)
        assert(result.profileImageUrl == loginMember.profileImageUrl)
        assert(result.selfIntroduction == loginMember.selfIntroduction)
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
        assert(result1)
        assert(!result2)
    }

    @Test
    fun `생년월일 수정 성공`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
        )
        val birthYear = 1997

        // mock
        every { memberAdapter.save(any()) } returns loginMember.copy(birthYear = birthYear)

        // when
        val result = memberService.updateBirthYear(loginMember, birthYear)

        // then
        assert(result.birthYear == birthYear)
    }

    @Test
    fun `프로필 정보 수정 성공 - 프로필 사진 수정 X`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
            nickname = "test-nickname",
            birthYear = 1997,
            profileImageUrl = "test-profile-image-url"
        )
        val request = UpdateProfileRequestDto(
            nickname = "test-nickname2",
            selfIntroduction = "test-self-introduction"
        )

        // mock
        every { memberAdapter.save(any()) } returns loginMember.copy(
            nickname = request.nickname,
            selfIntroduction = request.selfIntroduction
        )

        // when
        val result = memberService.updateProfile(loginMember, request)

        // then
        assert(result.nickname == request.nickname)
        assert(result.selfIntroduction == request.selfIntroduction)
        assert(result.profileImageUrl == null)
    }

//    // TODO: 프로필 사진 수정 O 테스트 코드 작성
//    @Test
//    fun `프로필 정보 수정 성공 - 프로필 사진 수정 O`() {
//    }
}