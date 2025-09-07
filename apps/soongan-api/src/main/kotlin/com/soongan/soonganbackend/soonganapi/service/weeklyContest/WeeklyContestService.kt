package com.soongan.soonganbackend.soonganapi.service.weeklyContest

import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.request.WeeklyContestPostRegisterRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.request.WeeklyContestPostUpdateRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response.MyWeeklyContestPostResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response.WeeklyContestListResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response.WeeklyContestPostListCursorResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response.WeeklyContestPostListResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response.WeeklyContestPostRegisterResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response.WeeklyContestPostResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response.WeeklyContestPostUpdateResponseDto
import com.soongan.soonganbackend.soonganapi.service.weeklyContest.validator.WeeklyContestValidator
import com.soongan.soonganbackend.soonganapi.service.weeklyContestPost.validator.WeeklyContestPostValidator
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.postLike.PostLikeAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal.WeeklyContestFinalAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.WeeklyContestPostOrderCriteriaEnum
import com.soongan.soonganbackend.soongansupport.service.GcpStorageService
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class WeeklyContestService(
    private val weeklyContestAdapter: WeeklyContestAdapter,
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val weeklyContestFinalAdapter: WeeklyContestFinalAdapter,
    private val gcpStorageService: GcpStorageService,
    private val weeklyContestPostValidator: WeeklyContestPostValidator,
    private val weeklyContestValidator: WeeklyContestValidator,
    private val likeAdapter: PostLikeAdapter
) {

    @Transactional(readOnly = true)
    fun getWeeklyContestList(): WeeklyContestListResponseDto {
        val weeklyContestList = weeklyContestAdapter.getAllWeeklyContests()
        return WeeklyContestListResponseDto.Companion.from(weeklyContestList)
    }

    @Transactional(readOnly = true)
    fun getWeeklyContestPost(postId: Long, loginMember: MemberEntity?): WeeklyContestPostResponseDto {
        val weeklyContestPost: WeeklyContestPostEntity = weeklyContestPostAdapter.getByIdOrNull(postId)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)
        val isTop7: Boolean = weeklyContestFinalAdapter.isTop7Post(weeklyContestPost)

        loginMember?.let {
            val isLiked: Boolean = likeAdapter.existsByPostIdAndContestTypeAndMember(postId, ContestTypeEnum.WEEKLY, loginMember)
            return WeeklyContestPostResponseDto.Companion.from(
                memberId = loginMember.id,
                weeklyContestPost = weeklyContestPost,
                isLiked = isLiked,
                isTop7 = isTop7,
                weeklyContest = weeklyContestPost.weeklyContest,
            )
        }


        return WeeklyContestPostResponseDto.Companion.from(
            weeklyContestPost =  weeklyContestPost,
            isTop7 = isTop7,
            weeklyContest = weeklyContestPost.weeklyContest,
        )
    }

    @Transactional(readOnly = true)
    fun getWeeklyContestPostList(
        round: Int?,
        orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        page: Int,
        pageSize: Int
    ): WeeklyContestPostListResponseDto {
        val weeklyContest = weeklyContestValidator.getWeeklyContestIfValidRound(round)

        val weeklyContestPostSlice: Slice<WeeklyContestPostEntity> = when (orderCriteria) {
            WeeklyContestPostOrderCriteriaEnum.LATEST -> {
                weeklyContestPostAdapter.getLatestPostWithSlicing(weeklyContest, page, pageSize)
            }

            WeeklyContestPostOrderCriteriaEnum.MOST_LIKED -> {
                weeklyContestPostAdapter.getMostLikedPostWithSlicing(weeklyContest, page, pageSize)
            }

            WeeklyContestPostOrderCriteriaEnum.OLDEST -> {
                weeklyContestPostAdapter.getOldestPostWithSlicing(weeklyContest, page, pageSize)
            }
        }

        return WeeklyContestPostListResponseDto.Companion.from(
            weeklyContest, weeklyContestPostSlice
        )
    }

    @Transactional(readOnly = true)
    fun getWeeklyContestPostListWithCursor(
        round: Int?,
        orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        currentCursor: String?,
        pageSize: Int
    ): WeeklyContestPostListCursorResponseDto {
        val weeklyContest = weeklyContestValidator.getWeeklyContestIfValidRound(round)

        val posts = when (orderCriteria) {
            WeeklyContestPostOrderCriteriaEnum.LATEST -> {
                weeklyContestPostAdapter.queryLatestPost(currentCursor, pageSize)
            }
            WeeklyContestPostOrderCriteriaEnum.MOST_LIKED -> {
                weeklyContestPostAdapter.queryMostLikedPost(currentCursor, pageSize)
            }
            WeeklyContestPostOrderCriteriaEnum.OLDEST -> {
                weeklyContestPostAdapter.queryOldestPost(currentCursor, pageSize)
            }
        }

        return WeeklyContestPostListCursorResponseDto.Companion.from(
            weeklyContest, posts
        )
    }

    @Transactional(readOnly = true)
    fun getMyWeeklyContestPostList(
        loginMember: MemberEntity,
        page: Int,
        pageSize: Int
    ): MyWeeklyContestPostResponseDto {
        val weeklyContestPostPage: Page<WeeklyContestPostEntity> = weeklyContestPostAdapter.getAllWeeklyContestPostByMember(
            loginMember, page, pageSize
        )

        return MyWeeklyContestPostResponseDto.Companion.from(weeklyContestPostPage)
    }


    @Transactional
    fun registerWeeklyContestPost(
        loginMember: MemberEntity,
        request: WeeklyContestPostRegisterRequestDto
    ): WeeklyContestPostRegisterResponseDto {
        val weeklyContest: WeeklyContestEntity =
            weeklyContestValidator.getWeeklyContestIfValidRound()

        weeklyContestPostValidator.validateMaxRegisterPost(weeklyContest, loginMember)

        val imageUrl = gcpStorageService.uploadContestImage(
            request.imageFile,
            loginMember.id,
            ContestTypeEnum.WEEKLY,
            weeklyContest.round
        )

        val savedPost = weeklyContestPostAdapter.save(
            WeeklyContestPostEntity(
                member = loginMember,
                title = request.title,
                weeklyContest = weeklyContest,
                imageUrl = imageUrl
            )
        )

        return WeeklyContestPostRegisterResponseDto(
            postId = savedPost.id,
            title = savedPost.title,
            imageUrl = savedPost.imageUrl,
            registerNickname = loginMember.nickname!!
        )
    }

    @Transactional
    fun updateWeeklyContestPost(loginMember: MemberEntity, postId: Long, weeklyContestPostRegisterRequest: WeeklyContestPostUpdateRequestDto): WeeklyContestPostUpdateResponseDto {
        val weeklyContest = weeklyContestValidator.getWeeklyContestIfValidRound()

        val now = LocalDateTime.now()
        if (weeklyContest.endAt.isBefore(now)) {
            throw SoonganException(StatusCode.SOONGAN_API_CANNOT_UPDATE_POST_AFTER_VOTE_END)
        }

        val validatedPost = weeklyContestPostValidator.validatePostOwner(loginMember, postId)
        val updatedPost = weeklyContestPostAdapter.save(validatedPost.copy(
            title = weeklyContestPostRegisterRequest.title,
        ))
        return WeeklyContestPostUpdateResponseDto(title = updatedPost.title)
    }

    @Transactional
    fun deleteMyWeeklyContestPost(
        loginMember: MemberEntity,
        postId: Long
    ) {
        weeklyContestPostValidator.validatePostOwner(loginMember, postId)
        weeklyContestPostAdapter.deleteWeeklyContestPost(postId)
    }
}