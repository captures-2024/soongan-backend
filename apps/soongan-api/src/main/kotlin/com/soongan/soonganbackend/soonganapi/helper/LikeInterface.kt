package com.soongan.soonganbackend.soonganapi.helper

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import org.springframework.transaction.annotation.Transactional

interface LikeInterface<RQ,RS> {

    @Transactional
    fun addLike(loginMember: MemberEntity, request: RQ): RS

    @Transactional
    fun cancelLike(loginMember: MemberEntity, request: RQ): RS

    /**
     * throw SOONGAN_API_DUPLICATED_LIKE
     */
    fun isDuplicateLike(id: Long, contestType: ContestTypeEnum, loginMember: MemberEntity)
}
