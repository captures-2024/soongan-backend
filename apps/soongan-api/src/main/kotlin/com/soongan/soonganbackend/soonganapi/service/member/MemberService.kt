package com.soongan.soonganbackend.soonganapi.service.member

import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.MemberInfoResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.UpdateBirthDateResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.UpdateNicknameResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.UpdateProfileImageResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganapi.service.gcp.GcpStorageService
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

        val updatedProfileImageUrl = gcpStorageService.uploadFile(profileImage, loginMember.id!!)
        val updatedMember = loginMember.copy(profileImageUrl = updatedProfileImageUrl)
        memberAdapter.save(updatedMember)
        return UpdateProfileImageResponseDto(
            updatedProfileImageUrl = updatedProfileImageUrl
        )
    }

    fun updateBirthDate(loginMember: MemberEntity, birthDate: LocalDate): UpdateBirthDateResponseDto {
        val updatedMember = loginMember.copy(birthDate = birthDate)
        memberAdapter.save(updatedMember)
        return UpdateBirthDateResponseDto(
            updatedBirthDate = birthDate
        )
    }
}