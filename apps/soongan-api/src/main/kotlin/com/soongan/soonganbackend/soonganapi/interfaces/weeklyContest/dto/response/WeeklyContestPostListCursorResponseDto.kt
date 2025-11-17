package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response


import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soonganpersistence.util.CursorResponseWrapper
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
    @Schema(description = "주간 콘테스트 게시글 정보 (좋아요/신고 수 포함)")
    data class WeeklyContestPostDto(
        @Schema(description = "게시글 작성자 닉네임", required = true)
        val nickname: String,

        @Schema(description = "게시글 작성자 프로필 이미지 URL", required = true)
        val profileImageUrl: String,

        @Schema(description = "게시글 ID", required = true)
        val postId: Long,

        @Schema(description = "좋아요 수", required = true)
        val likeCount: Int,

        @Schema(description = "게시글 이미지 URL", required = true)
        val imageUrl: String
    )

    companion object {
        private const val DEFAULT_PROFILE_IMAGE_URL = "profile_image_url"

        fun from(
            weeklyContest: WeeklyContestEntity,
            posts: CursorResponseWrapper<List<WeeklyContestPostEntity>>
        ): WeeklyContestPostListCursorResponseDto {
            return WeeklyContestPostListCursorResponseDto(
                round = weeklyContest.round,
                subject = weeklyContest.subject,
                posts = posts.data?.map {
                    WeeklyContestPostDto(
                        nickname = it.member.nickname!!,
                        profileImageUrl = it.member.profileImageUrl ?: DEFAULT_PROFILE_IMAGE_URL,
                        postId = it.id,
                        likeCount = it.likeCount,
                        imageUrl = it.imageUrl,
                    )
                } ?: emptyList(),
                nextCursor = posts.nextCursor
            )
        }
    }
}
