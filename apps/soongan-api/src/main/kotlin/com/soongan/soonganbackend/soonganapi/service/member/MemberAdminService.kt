package com.soongan.soonganbackend.soonganapi.service.member

import com.soongan.soonganbackend.soonganapi.admin.member.dto.request.GetMembersAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.member.dto.request.SearchByType
import com.soongan.soonganbackend.soonganapi.admin.member.dto.response.MemberAdminResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service

@Service
class MemberAdminService(
    private val memberAdapter: MemberAdapter
) {

    fun getAll(requestDto: GetMembersAdminRequestDto): List<MemberAdminResponseDto> {
        val (searchBy, searchKeyword) = requestDto
        return when (searchBy) {
            SearchByType.EMAIL -> {
                memberAdapter.getAllByEmail(email = searchKeyword).map { MemberAdminResponseDto.from(it) }
            }

            SearchByType.NICKNAME -> {
                memberAdapter.getAllByNickname(nickname = searchKeyword).map { MemberAdminResponseDto.from(it) }
            }
        }
    }

    fun deleteOne(memberId: Long): Unit {
        val optionalMember = memberAdapter.getById(memberId)
        if (optionalMember.isEmpty) throw SoonganException(StatusCode.NOT_FOUND, "해당 id로 회원이 존재하지 않습니다. id: $memberId")

        val member = optionalMember.get()
        if (member.withdrawalAt == null) throw SoonganException(StatusCode.BAD_REQUEST, "해당 유저는 탈퇴한 회원이 아닙니다.")
        memberAdapter.deleteOne(member.id)
    }
}