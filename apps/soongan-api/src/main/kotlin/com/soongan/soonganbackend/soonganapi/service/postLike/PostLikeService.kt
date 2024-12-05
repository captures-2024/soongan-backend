package com.soongan.soonganbackend.soonganapi.service.postLike

import com.soongan.soonganbackend.soonganapi.helper.LikeInterface
import com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto.request.PostLikeRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto.response.PostLikeResponseDto
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.postLike.PostLikeAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service

@Service
class PostLikeService(
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val postLikeAdapter: PostLikeAdapter
): LikeInterface<PostLikeRequestDto, PostLikeResponseDto>{


    override fun addLike(loginMember: MemberEntity, request: PostLikeRequestDto): PostLikeResponseDto {
        // todo: DAILY Contest 추가되면 구조 리팩토링 필요할 듯 (중복 발생할 듯)
        if (request.contestType == ContestTypeEnum.WEEKLY) {
            val updatedPost: WeeklyContestPostEntity = weeklyContestPostAdapter.getByIdOrNull(request.postId)?.let { post ->

                // 중복 좋아요 방지
                isDuplicateLike(post.id!!, request.contestType, loginMember)

                // 좋아요 추가
                postLikeAdapter.addLike(
                    request.postId,
                    request.contestType,
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


    override fun cancelLike(loginMember: MemberEntity, request: PostLikeRequestDto): PostLikeResponseDto {
        if (request.contestType == ContestTypeEnum.WEEKLY) {
            val updatedPost: WeeklyContestPostEntity = weeklyContestPostAdapter.getByIdOrNull(request.postId)?.let { post ->

                // 좋아요 취소
                postLikeAdapter.cancelLike(post.id!!, request.contestType, loginMember)

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

    override fun isDuplicateLike(id: Long, contestType: ContestTypeEnum, loginMember: MemberEntity) {
        if (postLikeAdapter.existsByPostIdAndContestTypeAndMember(id, contestType, loginMember)) {
            throw SoonganException(StatusCode.SOONGAN_API_DUPLICATED_LIKE)
        }
    }
}
