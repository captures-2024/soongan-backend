package com.soongan.soonganbackend.soonganpersistence.storage.persistence.postLike

import com.soongan.soonganbackend.soonganpersistence.storage.persistence.comment.ContestTypeEnum
import com.soongan.soonganbackend.soonganpersistence.storage.persistence.member.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeRepository: JpaRepository<PostLikeEntity, Long> {
    fun findByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity): PostLikeEntity?

    fun deleteByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity)
}
