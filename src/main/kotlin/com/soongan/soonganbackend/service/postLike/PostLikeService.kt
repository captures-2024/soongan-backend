package com.soongan.soonganbackend.service.postLike

import com.soongan.soonganbackend.interfaces.postLike.dto.PostLikeRequestDto
import com.soongan.soonganbackend.interfaces.postLike.dto.PostLikeResponseDto
import com.soongan.soonganbackend.persistence.member.MemberAdapter
import com.soongan.soonganbackend.persistence.member.MemberEntity
import com.soongan.soonganbackend.persistence.postLike.PostLikeAdapter
import com.soongan.soonganbackend.persistence.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.persistence.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import com.soongan.soonganbackend.util.domain.ContestTypeEnum
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostLikeService(
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val postLikeAdapter: PostLikeAdapter,
    private val memberAdapter: MemberAdapter
) {

    @Transactional
    fun addLikePost(loginMember: MemberEntity, postLikeRequest: PostLikeRequestDto): PostLikeResponseDto {
        // todo: DAILY Contest 추가되면 구조 리팩토링 필요할 듯 (중복 발생할 듯)
        if (postLikeRequest.contestType == ContestTypeEnum.WEEKLY) {
            val updatedPost: WeeklyContestPostEntity = weeklyContestPostAdapter.getByIdOrNull(postLikeRequest.postId)?.let { post ->

                // 중복 좋아요 방지
                if (isDuplicatedLike(post.id!!, postLikeRequest.contestType, loginMember)) {
                    throw SoonganException(StatusCode.SOONGAN_API_DUPLICATED_LIKE)
                }

                // 좋아요 추가
                postLikeAdapter.addLike(
                    postLikeRequest.postId,
                    postLikeRequest.contestType,
                    member = loginMember
                )

                // 좋아요 개수 증가
                weeklyContestPostAdapter.save(
                    post.copy(likeCount = post.likeCount + 1)
                )
            } ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)

            return PostLikeResponseDto(
                postId = updatedPost.id!!,
                likeCount = updatedPost.likeCount
            )
        } else {
            throw SoonganException(StatusCode.SOONGAN_API_INVALID_CONTEST_TYPE)
        }
    }

    @Transactional
    fun cancelLikePost(loginMember: MemberEntity, postLikeRequest: PostLikeRequestDto): PostLikeResponseDto {
        if (postLikeRequest.contestType == ContestTypeEnum.WEEKLY) {
            val updatedPost: WeeklyContestPostEntity = weeklyContestPostAdapter.getByIdOrNull(postLikeRequest.postId)?.let { post ->

                // 좋아요 취소
                postLikeAdapter.cancelLike(post.id!!, postLikeRequest.contestType, member)

                // 좋아요 개수 감소
                weeklyContestPostAdapter.save(
                    post.copy(likeCount = post.likeCount - 1)
                )
            } ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)

            return PostLikeResponseDto(
                postId = updatedPost.id!!,
                likeCount = updatedPost.likeCount
            )
        } else {
            throw SoonganException(StatusCode.SOONGAN_API_INVALID_CONTEST_TYPE)
        }
    }

    private fun isDuplicatedLike(postId: Long, contestType: ContestTypeEnum, member: MemberEntity): Boolean {
        return postLikeAdapter.getByPostIdAndContestTypeAndMember(postId, contestType, member)?.let { true } ?: false
    }
}
