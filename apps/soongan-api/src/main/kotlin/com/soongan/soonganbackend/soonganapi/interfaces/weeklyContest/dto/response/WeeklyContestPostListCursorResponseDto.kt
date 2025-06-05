package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response


import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.util.dto.CursorResponseDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주간 콘테스트 게시글 목록 응답 DTO")
data class WeeklyContestPostListCursorResponseDto(
    @Schema(description = "주간 콘테스트 라운드", required = true)
    val round: Int,

    @Schema(description = "주간 콘테스트 주제", required = true)
    val subject: String,

    @Schema(description = "게시글 목록", required = true)
    val posts: List<WeeklyContestPostDto>,

    @Schema(description = "다음 페이지에 요청으로 전달할 커서입니다. 마지막 페이지의 경우 생략됩니다.", required = false)
    val nextCursor: String? = null
) {
    data class WeeklyContestPostDto(
        val nickname: String,
        val profileImageUrl: String,
        val postId: Long,
        val imageUrl: String,
    )

    companion object {
        private const val DEFAULT_PROFILE_IMAGE_URL = "profile_image_url"

        fun from(
            weeklyContest: WeeklyContestEntity,
            posts: CursorResponseDto<List<WeeklyContestPostEntity>>
        ): WeeklyContestPostListCursorResponseDto {
            return WeeklyContestPostListCursorResponseDto(
                round = weeklyContest.round,
                subject = weeklyContest.subject,
                posts = posts.data?.map {
                    WeeklyContestPostDto(
                        nickname = it.member.nickname!!,
                        profileImageUrl = it.member.profileImageUrl ?: DEFAULT_PROFILE_IMAGE_URL,
                        postId = it.id,
                        imageUrl = it.imageUrl,
                    )
                } ?: emptyList(),
                nextCursor = posts.nextCursor
            )
        }
    }
}
