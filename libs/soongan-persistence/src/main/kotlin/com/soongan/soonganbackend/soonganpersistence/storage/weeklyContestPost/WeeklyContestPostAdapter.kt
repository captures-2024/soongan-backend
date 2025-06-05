package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost

import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.QWeeklyContestPostEntity.*
import com.soongan.soonganbackend.soonganpersistence.util.applySortCondition
import com.soongan.soonganbackend.soonganpersistence.util.calculateNextCursor
import com.soongan.soonganbackend.soonganpersistence.util.generateCursor
import com.soongan.soonganbackend.soongansupport.util.common.SortDirection
import com.soongan.soonganbackend.soongansupport.util.common.toEpochMilliKST
import com.soongan.soonganbackend.soonganpersistence.util.CursorResponseWrapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WeeklyContestPostAdapter(
    private val weeklyContestPostRepository: WeeklyContestPostRepository,
    private val queryFactory: JPAQueryFactory
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
    fun getAllWeeklyContestPostByMemberAndWeeklyContest(
        member: MemberEntity,
        weeklyContest: WeeklyContestEntity
    ): List<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByMemberAndWeeklyContestOrderByCreatedAtDesc(
            member,
            weeklyContest
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

    override fun queryLatestPost(currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>> {
        val weeklyContestPost = weeklyContestPostEntity
        val cursorExpression: StringExpression = generateCursor(weeklyContestPost.createdAt, weeklyContestPost.id)

        val query: JPAQuery<WeeklyContestPostEntity> = queryFactory
            .selectFrom(weeklyContestPost)
            .orderBy(weeklyContestPost.createdAt.desc(), weeklyContestPost.id.desc())
            .limit(size.toLong())
            .applySortCondition(cursorExpression, currentCursor, SortDirection.DESC)

        val posts = query.fetch()

        val nextCursor = calculateNextCursor(posts) { listOf(it.createdAt.toEpochMilliKST(), it.id) }

        return CursorResponseWrapper.from(nextCursor, posts)

    }

    override fun queryOldestPost(currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>> {
        val weeklyContestPost = weeklyContestPostEntity
        val cursorExpression: StringExpression = generateCursor(weeklyContestPost.createdAt, weeklyContestPost.id)

        val query: JPAQuery<WeeklyContestPostEntity> = queryFactory
            .selectFrom(weeklyContestPost)
            .orderBy(weeklyContestPost.createdAt.asc(), weeklyContestPost.id.desc())
            .limit(size.toLong())
            .applySortCondition(cursorExpression, currentCursor, SortDirection.ASC)

        val posts = query.fetch()

        val nextCursor = calculateNextCursor(posts) { listOf(it.createdAt.toEpochMilliKST(), it.id) }

        return CursorResponseWrapper.from(nextCursor, posts)
    }

    override fun queryMostLikedPost(currentCursor: String?, size: Int): CursorResponseWrapper<List<WeeklyContestPostEntity>> {
        val weeklyContestPost = weeklyContestPostEntity
        val cursorExpression: StringExpression = generateCursor(weeklyContestPost.likeCount, weeklyContestPost.id)

        val query: JPAQuery<WeeklyContestPostEntity> = queryFactory
            .selectFrom(weeklyContestPost)
            .orderBy(weeklyContestPost.likeCount.desc(), weeklyContestPost.id.desc())
            .limit(size.toLong())
            .applySortCondition(cursorExpression, currentCursor, SortDirection.DESC)

        val posts = query.fetch()

        val nextCursor = calculateNextCursor(posts) { listOf(it.likeCount, it.id) }

        return CursorResponseWrapper.from(nextCursor, posts)
    }
}
