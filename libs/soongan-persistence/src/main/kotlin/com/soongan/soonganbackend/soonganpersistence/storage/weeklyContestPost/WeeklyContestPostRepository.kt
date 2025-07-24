package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.type.WeeklyContestPostAndIsLiked
import com.soongan.soonganbackend.soonganpersistence.util.CursorResponseWrapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface WeeklyContestPostRepository : JpaRepository<WeeklyContestPostEntity, Long> {

    // 현재 회차 최신순
    fun findAllByWeeklyContestOrderByCreatedAtDesc(
        weeklyContestEntity: WeeklyContestEntity,
        pageable: Pageable
    ): Slice<WeeklyContestPostEntity>

    // 현재 회차 오래된 순
    fun findAllByWeeklyContestOrderByCreatedAtAsc(
        weeklyContestEntity: WeeklyContestEntity,
        pageable: Pageable
    ): Slice<WeeklyContestPostEntity>

    // 현재 회차 좋아요 많은 순
    fun findAllByWeeklyContestOrderByLikeCountDesc(
        weeklyContestEntity: WeeklyContestEntity,
        pageable: Pageable
    ): Slice<WeeklyContestPostEntity>


    fun countByWeeklyContestAndMember(
        weeklyContestEntity: WeeklyContestEntity,
        member: MemberEntity
    ): Int

    fun findAllByMember(
        member: MemberEntity,
        pageable: Pageable
    ): Page<WeeklyContestPostEntity>

    fun findAllByMemberAndWeeklyContestOrderByCreatedAtDesc(
        member: MemberEntity,
        weeklyContestEntity: WeeklyContestEntity
    ): List<WeeklyContestPostEntity>

    fun countByWeeklyContestId(weeklyContestId: Long): Int

    // `JOIN`을 통해 게시물과 좋아요 여부를 함께 가져오는 쿼리
    @Query("""
        SELECT new com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.type.WeeklyContestPostAndIsLiked(
            p, 
            CASE 
                WHEN EXISTS (
                    SELECT 1 
                    FROM PostLikeEntity pl 
                    WHERE pl.postId = p.id 
                    AND pl.contestType = 'WEEKLY' 
                    AND pl.member.id = :memberId
                ) 
                THEN TRUE
                ELSE FALSE 
            END
        )
        FROM WeeklyContestPostEntity p
        LEFT JOIN p.weeklyContest wc
        WHERE wc.id = :weeklyContestId
            AND EXISTS (
                SELECT 1 
                FROM PostLikeEntity pl 
                WHERE pl.postId = p.id 
                AND pl.member.id = :memberId
            )
    """)
    fun findHomePostsWithIsLiked(
        @Param("memberId") memberId: Long,
        @Param("weeklyContestId") weeklyContestId: Long,
    ): Slice<WeeklyContestPostAndIsLiked>
}

interface WeeklyContestPostRepositoryCustom {

    // 현재 회차 최신순
    fun queryLatestPost(currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>>

    // 현재 회차 오래된 순
    fun queryOldestPost(currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>>

    // 현재 회차 좋아요 많은 순
    fun queryMostLikedPost(currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>>
}
