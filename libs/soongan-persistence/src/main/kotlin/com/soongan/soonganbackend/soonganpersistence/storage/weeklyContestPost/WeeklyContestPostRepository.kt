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

    // 현재 회차 최신순 (신고 3회 이상 게시물 제외)
    @Query("""
        SELECT p
        FROM WeeklyContestPostEntity p
        WHERE p.weeklyContest = :weeklyContestEntity
            AND (
                SELECT COUNT(r)
                FROM ReportEntity r
                WHERE r.targetId = p.id
                    AND r.targetType = 'WEEKLY_POST'
            ) < 3
        ORDER BY p.createdAt DESC
    """)
    fun findAllByWeeklyContestOrderByCreatedAtDesc(
        @Param("weeklyContestEntity") weeklyContestEntity: WeeklyContestEntity,
        pageable: Pageable
    ): Slice<WeeklyContestPostEntity>

    // 현재 회차 오래된 순 (신고 3회 이상 게시물 제외)
    @Query("""
        SELECT p
        FROM WeeklyContestPostEntity p
        WHERE p.weeklyContest = :weeklyContestEntity
            AND (
                SELECT COUNT(r)
                FROM ReportEntity r
                WHERE r.targetId = p.id
                    AND r.targetType = 'WEEKLY_POST'
            ) < 3
        ORDER BY p.createdAt ASC
    """)
    fun findAllByWeeklyContestOrderByCreatedAtAsc(
        @Param("weeklyContestEntity") weeklyContestEntity: WeeklyContestEntity,
        pageable: Pageable
    ): Slice<WeeklyContestPostEntity>

    // 현재 회차 좋아요 많은 순 (신고 3회 이상 게시물 제외)
    @Query("""
        SELECT p
        FROM WeeklyContestPostEntity p
        WHERE p.weeklyContest = :weeklyContestEntity
            AND (
                SELECT COUNT(r)
                FROM ReportEntity r
                WHERE r.targetId = p.id
                    AND r.targetType = 'WEEKLY_POST'
            ) < 3
        ORDER BY p.likeCount DESC
    """)
    fun findAllByWeeklyContestOrderByLikeCountDesc(
        @Param("weeklyContestEntity") weeklyContestEntity: WeeklyContestEntity,
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

    // 해당 콘테스트의 1등 게시물 조회 (좋아요 많은 순, 동점 시 업로드 시간 빠른 순)
    @Query("""
        SELECT p
        FROM WeeklyContestPostEntity p
        WHERE p.weeklyContest = :weeklyContest
            AND p.deletedAt IS NULL
            AND p.blindedAt IS NULL
            AND (
                SELECT COUNT(r)
                FROM ReportEntity r
                WHERE r.targetId = p.id
                    AND r.targetType = 'WEEKLY_POST'
            ) < 3
        ORDER BY p.likeCount DESC, p.createdAt ASC
        LIMIT 1
    """)
    fun findFirstPlacePost(@Param("weeklyContest") weeklyContest: WeeklyContestEntity): WeeklyContestPostEntity?

    // 해당 콘테스트의 상위 7개 게시물 조회 (좋아요 많은 순, 동점 시 업로드 시간 빠른 순)
    @Query("""
        SELECT p
        FROM WeeklyContestPostEntity p
        WHERE p.weeklyContest = :weeklyContest
            AND p.deletedAt IS NULL
            AND p.blindedAt IS NULL
            AND (
                SELECT COUNT(r)
                FROM ReportEntity r
                WHERE r.targetId = p.id
                    AND r.targetType = 'WEEKLY_POST'
            ) < 3
        ORDER BY p.likeCount DESC, p.createdAt ASC
        LIMIT 7
    """)
    fun findTop7Posts(@Param("weeklyContest") weeklyContest: WeeklyContestEntity): List<WeeklyContestPostEntity>

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
        WHERE p.weeklyContest.id = :weeklyContestId
            AND p.member.id = :memberId
    """)
    fun findHomePostsWithIsLiked(
        @Param("memberId") memberId: Long,
        @Param("weeklyContestId") weeklyContestId: Long,
    ): List<WeeklyContestPostAndIsLiked>
}

interface WeeklyContestPostRepositoryCustom {

    // 현재 회차 최신순
    fun queryLatestPost(weeklyContest: WeeklyContestEntity, currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>>

    // 현재 회차 오래된 순
    fun queryOldestPost(weeklyContest: WeeklyContestEntity, currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>>

    // 현재 회차 좋아요 많은 순
    fun queryMostLikedPost(weeklyContest: WeeklyContestEntity, currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>>
}
