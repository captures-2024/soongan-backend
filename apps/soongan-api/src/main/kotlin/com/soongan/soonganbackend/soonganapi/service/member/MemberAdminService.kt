package com.soongan.soonganbackend.soonganapi.service.member

import com.soongan.soonganbackend.soonganapi.admin.member.dto.request.GetMembersAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.member.dto.request.SearchByType
import com.soongan.soonganbackend.soonganapi.admin.member.dto.response.MemberAdminResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
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
}