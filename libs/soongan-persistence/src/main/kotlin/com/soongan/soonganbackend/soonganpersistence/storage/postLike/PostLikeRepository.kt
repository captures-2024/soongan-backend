package com.soongan.soonganbackend.soonganpersistence.storage.postLike

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeRepository: JpaRepository<PostLikeEntity, Long> {
    fun findByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity): PostLikeEntity?

    fun deleteByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity)
}
