package com.soongan.soonganbackend.soonganapi.service.member

import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.request.UpdateProfileRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.*
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.service.GcpStorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@Service
class MemberService(
    private val memberAdapter: MemberAdapter,
    private val gcpStorageService: GcpStorageService,
) {
    fun getMemberInfo(loginMember: MemberEntity): MemberInfoResponseDto {
        return MemberInfoResponseDto.from(loginMember)
    }

    @Transactional(readOnly = true)
    fun checkNickname(nickname: String): Boolean {
        return memberAdapter.getByNickname(nickname) == null
    }

    @Transactional
    fun updateNickname(loginMember: MemberEntity, newNickname: String): UpdateNicknameResponseDto {
        val updatedMember = loginMember.copy(nickname = newNickname)
        memberAdapter.save(updatedMember)

        return UpdateNicknameResponseDto(
            updatedNickname = newNickname
        )
    }

    @Transactional
    fun updateProfileImage(loginMember: MemberEntity, profileImage: MultipartFile): UpdateProfileImageResponseDto {
        if (loginMember.profileImageUrl != null) {
            gcpStorageService.deleteFile(loginMember.profileImageUrl!!)
        }

        val updatedProfileImageUrl = gcpStorageService.uploadProfileImage(profileImage, loginMember.id!!)
        val updatedMember = loginMember.copy(profileImageUrl = updatedProfileImageUrl)
        memberAdapter.save(updatedMember)
        return UpdateProfileImageResponseDto(
            updatedProfileImageUrl = updatedProfileImageUrl
        )
    }

    fun updateBirthDate(loginMember: MemberEntity, birthDate: LocalDate): UpdateBirthDateResponseDto {
        if (loginMember.birthDate != birthDate) {
            val updatedMember = loginMember.copy(birthDate = birthDate)
            memberAdapter.save(updatedMember)
        }
        return UpdateBirthDateResponseDto(
            updatedBirthDate = birthDate
        )
    }

    fun updateProfile(loginMember: MemberEntity, request: UpdateProfileRequestDto): UpdateProfileResponseDto {
        val oldProfileImageUrl = loginMember.profileImageUrl
        val updateProfileImageUrl = request.profileImage?.let {
            gcpStorageService.uploadProfileImage(it, loginMember.id!!)
        }

        val updatedMember = loginMember.copy(
            nickname = request.nickname ?: loginMember.nickname,
            selfIntroduction = request.selfIntroduction ?: loginMember.selfIntroduction,
            profileImageUrl = updateProfileImageUrl ?: oldProfileImageUrl
        )
        memberAdapter.save(updatedMember)

        if (updateProfileImageUrl != null) {
            oldProfileImageUrl?.let {
                if (oldProfileImageUrl != updateProfileImageUrl) {
                    gcpStorageService.deleteFile(oldProfileImageUrl)
                }
            }
        }

        return UpdateProfileResponseDto(
            newNickname = request.nickname,
            newSelfIntroduction = request.selfIntroduction,
            newProfileImageUrl = updateProfileImageUrl
        )
    }
}