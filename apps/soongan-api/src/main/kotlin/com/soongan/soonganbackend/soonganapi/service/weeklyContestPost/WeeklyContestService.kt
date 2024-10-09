package com.soongan.soonganbackend.soonganapi.service.weeklyContestPost

import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.WeeklyContestPostRegisterRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.WeeklyContestPostRegisterResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.WeeklyContestPostResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soonganapi.service.gcp.GcpStorageService
import com.soongan.soonganbackend.soonganapi.service.weeklyContest.WeeklyContestValidator
import com.soongan.soonganbackend.soonganapi.service.weeklyContestPost.WeeklyContestPostOrderCriteriaEnum.*
import com.soongan.soonganbackend.soongansupport.util.dto.PageDto
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WeeklyContestService(
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val weeklyContestAdapter: WeeklyContestAdapter,
    private val gcpStorageService: GcpStorageService,
    private val weeklyContestPostValidator: WeeklyContestPostValidator,
    private val weeklyContestValidator: WeeklyContestValidator
) {
    companion object {
        private const val DEFAULT_NICKNAME = "nickname"
        private const val DEFAULT_PROFILE_IMAGE_URL = "profile_image_url"
    }

    @Transactional(readOnly = true)
    fun getWeeklyContestPost(
        round: Int,
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

        return WeeklyContestPostResponseDto(
            round = round,
            subject = weeklyContest.subject,
            posts = weeklyContestPostSlice.content.map {
                WeeklyContestPostResponseDto.WeeklyContestPostDto(
                    nickname = it.member.nickname ?: DEFAULT_NICKNAME,
                    profileImageUrl = it.member.profileImageUrl ?: DEFAULT_PROFILE_IMAGE_URL,
                    postId = it.id!!,
                    imageUrl = it.imageUrl,
                )
            },
            pageInfo = PageDto(
                page = weeklyContestPostSlice.number,
                size = weeklyContestPostSlice.size,
                hasNext = weeklyContestPostSlice.hasNext()
            )
        )
    }

    @Transactional
    fun registerWeeklyContestPost(
        loginMember: MemberEntity,
        weeklyContestPostRegisterRequest: WeeklyContestPostRegisterRequestDto
    ): WeeklyContestPostRegisterResponseDto {
        val weeklyContest: WeeklyContestEntity =
            weeklyContestValidator.getWeeklyContestIfValidRound(weeklyContestPostRegisterRequest.weeklyContestRound)

        weeklyContestPostValidator.validateMaxRegisterPost(weeklyContest, loginMember)

        val imageUrl = gcpStorageService.uploadFile(
            weeklyContestPostRegisterRequest.imageFile,
            loginMember.id!!
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
}
