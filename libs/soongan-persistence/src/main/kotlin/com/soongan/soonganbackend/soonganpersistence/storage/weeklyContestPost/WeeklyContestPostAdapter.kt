package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost

import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.report.QReportEntity.reportEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.QWeeklyContestPostEntity.weeklyContestPostEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.type.WeeklyContestPostAndIsLiked
import com.soongan.soonganbackend.soonganpersistence.util.applySortCondition
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import com.soongan.soonganbackend.soongansupport.util.common.SortDirection
import com.soongan.soonganbackend.soonganpersistence.util.CursorResponseWrapper
import com.soongan.soonganbackend.soonganpersistence.util.DateTimeCursorSpec
import com.soongan.soonganbackend.soonganpersistence.util.IntegerCursorSpec
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WeeklyContestPostAdapter(
    private val weeklyContestPostRepository: WeeklyContestPostRepository,
    private val queryFactory: JPAQueryFactory,
    private val datetimeCursorSpec: DateTimeCursorSpec,
    private val integerCursorSpec: IntegerCursorSpec
): WeeklyContestPostRepositoryCustom {

    @Transactional
    fun save(post: WeeklyContestPostEntity): WeeklyContestPostEntity {
        return weeklyContestPostRepository.save(post)
    }

    @Transactional(readOnly = true)
    fun getByIdOrNull(postId: Long): WeeklyContestPostEntity? {
        return weeklyContestPostRepository.findByIdOrNull(postId)
    }

    @Transactional(readOnly = true)
    fun getLatestPostWithSlicing(
        weeklyContest: WeeklyContestEntity,
        page: Int,
        size: Int
    ): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestOrderByCreatedAtDesc(
            weeklyContest,
            PageRequest.of(page, size)
        )
    }

    @Transactional(readOnly = true)
    fun getOldestPostWithSlicing(
        weeklyContest: WeeklyContestEntity,
        page: Int,
        size: Int
    ): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestOrderByCreatedAtAsc(
            weeklyContest,
            PageRequest.of(page, size)
        )
    }

    @Transactional(readOnly = true)
    fun getMostLikedPostWithSlicing(
        weeklyContest: WeeklyContestEntity,
        page: Int,
        size: Int
    ): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestOrderByLikeCountDesc(
            weeklyContest,
            PageRequest.of(page, size)
        )
    }

    @Transactional(readOnly = true)
    fun countRegisteredPostByMember(
        weeklyContest: WeeklyContestEntity,
        member: MemberEntity,
    ): Int {
        return weeklyContestPostRepository.countByWeeklyContestAndMember(weeklyContest, member)
    }

    @Transactional(readOnly = true)
    fun getAllWeeklyContestPostByMember(
        member: MemberEntity,
        page: Int,
        size: Int
    ): Page<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByMember(
            member,
            PageRequest.of(page, size)
        )
    }

    @Transactional(readOnly = true)
    fun getHomePostsWithIsLiked(
        member: MemberEntity,
        weeklyContest: WeeklyContestEntity
    ): List<WeeklyContestPostAndIsLiked> {
        return weeklyContestPostRepository.findHomePostsWithIsLiked(
            member.id,
            weeklyContest.id
        )
    }

    @Transactional
    fun deleteWeeklyContestPost(postId: Long) {
        weeklyContestPostRepository.deleteById(postId)
    }

    @Transactional(readOnly = true)
    fun countByWeeklyContestId(contestId: Long): Int {
        return weeklyContestPostRepository.countByWeeklyContestId(contestId)
    }

    override fun queryLatestPost(weeklyContest: WeeklyContestEntity, currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>> {
        val retrieveSize = size + 1

        val cursorExpression = datetimeCursorSpec.generateCursorExpression(
            sortCriteria = weeklyContestPostEntity.createdAt,
            pk = weeklyContestPostEntity.id.stringValue()
        )

        // 신고 3회 이상 게시물 제외 조건
        val reportCountSubquery = JPAExpressions
            .select(reportEntity.count())
            .from(reportEntity)
            .where(
                reportEntity.targetId.eq(weeklyContestPostEntity.id),
                reportEntity.targetType.eq(ReportTargetTypeEnum.WEEKLY_POST)
            )

        val query: JPAQuery<WeeklyContestPostEntity> = queryFactory
            .selectFrom(weeklyContestPostEntity)
            .where(
                weeklyContestPostEntity.weeklyContest.eq(weeklyContest),
                reportCountSubquery.lt(3L)
            )
            .applySortCondition(cursorExpression, currentCursor, SortDirection.DESC)
            .orderBy(weeklyContestPostEntity.createdAt.desc(), weeklyContestPostEntity.id.desc())
            .limit(retrieveSize.toLong())

        val posts = query.fetch()

        // 다음 페이지 존재 여부 판단
        val hasNext = posts.size > size
        val actualPosts = if (hasNext) posts.dropLast(1) else posts

        val nextCursor = if (hasNext) {
            datetimeCursorSpec.generateCursor(actualPosts.last().createdAt, actualPosts.last().id)
        } else null

        return CursorResponseWrapper.from(nextCursor, actualPosts)

    }

    override fun queryOldestPost(weeklyContest: WeeklyContestEntity, currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>> {
        val retrieveSize = size + 1

        val cursorExpression = datetimeCursorSpec.generateCursorExpression(
            sortCriteria = weeklyContestPostEntity.createdAt,
            pk = weeklyContestPostEntity.id.stringValue()
        )

        // 신고 3회 이상 게시물 제외 조건
        val reportCountSubquery = JPAExpressions
            .select(reportEntity.count())
            .from(reportEntity)
            .where(
                reportEntity.targetId.eq(weeklyContestPostEntity.id),
                reportEntity.targetType.eq(ReportTargetTypeEnum.WEEKLY_POST)
            )

        val query: JPAQuery<WeeklyContestPostEntity> = queryFactory
            .selectFrom(weeklyContestPostEntity)
            .where(
                weeklyContestPostEntity.weeklyContest.eq(weeklyContest),
                reportCountSubquery.lt(3L)
            )
            .applySortCondition(cursorExpression, currentCursor, SortDirection.ASC)
            .orderBy(weeklyContestPostEntity.createdAt.desc(), weeklyContestPostEntity.id.desc())
            .limit(retrieveSize.toLong())

        val posts = query.fetch()

        // 다음 페이지 존재 여부 판단
        val hasNext = posts.size > size
        val actualPosts = if (hasNext) posts.dropLast(1) else posts

        val nextCursor = if (hasNext) {
            datetimeCursorSpec.generateCursor(actualPosts.last().createdAt, actualPosts.last().id)
        } else null

        return CursorResponseWrapper.from(nextCursor, actualPosts)
    }

    override fun queryMostLikedPost(weeklyContest: WeeklyContestEntity, currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>> {
        val retrieveSize = size + 1

        val cursorExpression = integerCursorSpec.generateCursorExpression(
            sortCriteria = weeklyContestPostEntity.likeCount,
            pk = weeklyContestPostEntity.id.stringValue()
        )

        // 신고 3회 이상 게시물 제외 조건
        val reportCountSubquery = JPAExpressions
            .select(reportEntity.count())
            .from(reportEntity)
            .where(
                reportEntity.targetId.eq(weeklyContestPostEntity.id),
                reportEntity.targetType.eq(ReportTargetTypeEnum.WEEKLY_POST)
            )

        val query: JPAQuery<WeeklyContestPostEntity> = queryFactory
            .selectFrom(weeklyContestPostEntity)
            .where(
                weeklyContestPostEntity.weeklyContest.eq(weeklyContest),
                reportCountSubquery.lt(3L)
            )
            .applySortCondition(cursorExpression, currentCursor, SortDirection.DESC)
            .orderBy(weeklyContestPostEntity.likeCount.desc(), weeklyContestPostEntity.id.desc())
            .limit(retrieveSize.toLong())

        val posts = query.fetch()

        // 다음 페이지 존재 여부 판단
        val hasNext = posts.size > size
        val actualPosts = if (hasNext) posts.dropLast(1) else posts

        val nextCursor = if (hasNext) {
            datetimeCursorSpec.generateCursor(actualPosts.last().createdAt, actualPosts.last().id)
        } else null

        return CursorResponseWrapper.from(nextCursor, actualPosts)
    }
}
