package com.soongan.soonganbackend.soonganapi.service.member

import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.request.UpdateProfileRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.*
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportAdapter
import com.soongan.soonganbackend.soongansupport.service.GcpStorageService
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberAdapter: MemberAdapter,
    private val reportAdapter: ReportAdapter,
    private val gcpStorageService: GcpStorageService,
) {
    fun getMemberInfo(loginMember: MemberEntity): MemberInfoResponseDto {
        val reportHistories = reportAdapter.getReportHistoriesByReportMember(loginMember)
        return MemberInfoResponseDto.from(loginMember, reportHistories)
    }

    @Transactional(readOnly = true)
    fun checkEnableNickname(nickname: String): Boolean {
        return memberAdapter.getByNickname(nickname) == null
    }

    @Transactional
    fun updateBirthYear(loginMember: MemberEntity, birthYear: Int): UpdateBirthYearResponseDto {
        if (loginMember.birthYear != birthYear) {
            val updatedMember = loginMember.copy(birthYear = birthYear)
            memberAdapter.save(updatedMember)
        }
        return UpdateBirthYearResponseDto(
            birthYear = birthYear
        )
    }

    @Transactional
    fun updateProfile(loginMember: MemberEntity, request: UpdateProfileRequestDto): UpdateProfileResponseDto {
        if (request.nickname != null && request.nickname != loginMember.nickname && checkEnableNickname(request.nickname).not()) {
            throw SoonganException(StatusCode.SOONGAN_API_DUPLICATED_NICKNAME, "닉네임이 중복됩니다.")
        }

        if (request.isDefaultProfileImage && request.profileImage != null) {
            throw SoonganException(StatusCode.BAD_REQUEST, "기본 프로필 이미지를 사용할 경우 프로필 이미지를 업로드할 수 없습니다.")
        }

        val oldProfileImageUrl = loginMember.profileImageUrl
        val updatedProfileImageUrl = request.profileImage?.let {
            gcpStorageService.uploadProfileImage(it, loginMember.id!!)
        }

        val updatedMember = loginMember.copy(
            nickname = request.nickname ?: loginMember.nickname,
            selfIntroduction = request.selfIntroduction ?: loginMember.selfIntroduction,
            profileImageUrl = if (request.isDefaultProfileImage) null else updatedProfileImageUrl ?: oldProfileImageUrl
        )
        memberAdapter.save(updatedMember)

        if (oldProfileImageUrl != null && (request.isDefaultProfileImage || updatedProfileImageUrl != null)) {
            if (oldProfileImageUrl != updatedProfileImageUrl) {
                gcpStorageService.deleteFile(oldProfileImageUrl)
            }
        }

        return UpdateProfileResponseDto(
            nickname = request.nickname,
            selfIntroduction = request.selfIntroduction,
            profileImageUrl = updatedProfileImageUrl
        )
    }
}