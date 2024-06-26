package com.soongan.soonganbackend.persistence.postLike

import com.soongan.soonganbackend.persistence.member.MemberEntity
import com.soongan.soonganbackend.util.domain.ContestTypeEnum
import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeRepository: JpaRepository<PostLikeEntity, Long> {
    fun findByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity): PostLikeEntity?
}
