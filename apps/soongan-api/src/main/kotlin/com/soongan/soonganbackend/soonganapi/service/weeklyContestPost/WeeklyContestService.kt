package com.soongan.soonganbackend.soonganapi.service.weeklyContestPost

import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response.MyWeeklyContestPostResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.request.WeeklyContestPostRegisterRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response.WeeklyContestPostRegisterResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response.WeeklyContestPostResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.service.GcpStorageService
import com.soongan.soonganbackend.soonganapi.service.weeklyContest.validator.WeeklyContestValidator
import com.soongan.soonganbackend.soongansupport.domain.WeeklyContestPostOrderCriteriaEnum.*
import com.soongan.soonganbackend.soonganapi.service.weeklyContestPost.validator.WeeklyContestPostValidator
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.WeeklyContestPostOrderCriteriaEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WeeklyContestService(
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val gcpStorageService: GcpStorageService,
    private val weeklyContestPostValidator: WeeklyContestPostValidator,
    private val weeklyContestValidator: WeeklyContestValidator
) {

    @Transactional(readOnly = true)
    fun getWeeklyContestPostList(
        round: Int?,
        orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        page: Int,
        pageSize: Int
    ): WeeklyContestPostResponseDto {
        val weeklyContest = weeklyContestValidator.getWeeklyContestIfValidRound(round)

        val weeklyContestPostSlice: Slice<WeeklyContestPostEntity> = when (orderCriteria) {
            LATEST -> {
                weeklyContestPostAdapter.getLatestPostWithSlicing(weeklyContest, page, pageSize)
            }

            MOST_LIKED -> {
                weeklyContestPostAdapter.getMostLikedPostWithSlicing(weeklyContest, page, pageSize)
            }

            OLDEST -> {
                weeklyContestPostAdapter.getOldestPostWithSlicing(weeklyContest, page, pageSize)
            }
        }

        return WeeklyContestPostResponseDto.from(
            weeklyContest, weeklyContestPostSlice
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

        return MyWeeklyContestPostResponseDto.from(weeklyContestPostPage)
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
            loginMember.id!!,
            ContestTypeEnum.WEEKLY,
            weeklyContest.round
        )

        val savedPost = weeklyContestPostAdapter.save(
            WeeklyContestPostEntity(
                member = loginMember,
                weeklyContest = weeklyContest,
                imageUrl = imageUrl
            )
        )

        return WeeklyContestPostRegisterResponseDto(
            postId = savedPost.id!!,
            subject = weeklyContest.subject,
            imageUrl = savedPost.imageUrl,
            registerNickname = loginMember.nickname!!
        )
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
