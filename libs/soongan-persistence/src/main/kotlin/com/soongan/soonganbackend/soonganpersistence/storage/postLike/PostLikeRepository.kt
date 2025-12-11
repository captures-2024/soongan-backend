package com.soongan.soonganbackend.soonganpersistence.storage.postLike

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface PostLikeRepository: JpaRepository<PostLikeEntity, Long> {

    fun existsByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity): Boolean

    fun deleteByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity)

    // 특정 기간 내에 받은 좋아요 수를 집계
    @Query("""
        SELECT COUNT(pl)
        FROM PostLikeEntity pl
        WHERE pl.postId = :postId
            AND pl.contestType = :contestType
            AND pl.createdAt >= :startAt
            AND pl.createdAt <= :endAt
    """)
    fun countLikesByPostIdAndPeriod(
        @Param("postId") postId: Long,
        @Param("contestType") contestType: ContestTypeEnum,
        @Param("startAt") startAt: LocalDateTime,
        @Param("endAt") endAt: LocalDateTime
    ): Long
}
