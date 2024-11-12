package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.util.dto.PageDto
import org.springframework.data.domain.Slice


data class WeeklyContestPostResponseDto(
    val round: Int,
    val subject: String,
    val posts: List<WeeklyContestPostDto>,
    val pageInfo: PageDto
) {

    data class WeeklyContestPostDto(
        val nickname: String,
        val profileImageUrl: String,
        val postId: Long,
        val imageUrl: String,
    )

    companion object {
        private const val DEFAULT_NICKNAME = "nickname"
        private const val DEFAULT_PROFILE_IMAGE_URL = "profile_image_url"
        fun from(
            weeklyContest: WeeklyContestEntity,
            postSlice: Slice<WeeklyContestPostEntity>
        ): WeeklyContestPostResponseDto {
            return WeeklyContestPostResponseDto(
                round = weeklyContest.round,
                subject = weeklyContest.subject,
                posts = postSlice.content.map {
                    WeeklyContestPostDto(
                        nickname = it.member.nickname ?: DEFAULT_NICKNAME,
                        profileImageUrl = it.member.profileImageUrl ?: DEFAULT_PROFILE_IMAGE_URL,
                        postId = it.id!!,
                        imageUrl = it.imageUrl,
                    )
                },
                pageInfo = PageDto(
                    page = postSlice.number,
                    size = postSlice.size,
                    hasNext = postSlice.hasNext()
                )
            )
        }
    }

}
