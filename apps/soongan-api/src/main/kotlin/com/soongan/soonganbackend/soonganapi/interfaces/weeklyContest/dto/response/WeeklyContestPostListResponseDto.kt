package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.util.dto.PageDto
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Slice

@Schema(description = "주간 콘테스트 게시글 목록 응답 DTO")
data class WeeklyContestPostListResponseDto(
    @Schema(description = "주간 콘테스트 라운드", required = true)
    val round: Int,

    @Schema(description = "주간 콘테스트 주제", required = true)
    val subject: String,

    @Schema(description = "게시글 목록", required = true)
    val posts: List<WeeklyContestPostDto>,

    @Schema(description = "페이지네이션 정보", required = true)
    val pageInfo: PageDto
) {

    @Schema(description = "주간 콘테스트 게시글 정보")
    data class WeeklyContestPostDto(
        @Schema(description = "게시글 작성자 닉네임")
        val nickname: String,

        @Schema(description = "게시글 작성자 프로필 이미지 URL")
        val profileImageUrl: String,

        @Schema(description = "게시글 ID")
        val postId: Long,

        @Schema(description = "게시글 이미지 URL")
        val imageUrl: String,

        @Schema(description = "이미지 가로세로 비율 (width / height)")
        val ratio: Double?,

        @Schema(description = "신고 수")
        val reportCount: Long
    )

    companion object {
        private const val DEFAULT_PROFILE_IMAGE_URL = "profile_image_url"
        fun from(
            weeklyContest: WeeklyContestEntity,
            postSlice: Slice<WeeklyContestPostEntity>
        ): WeeklyContestPostListResponseDto {
            return WeeklyContestPostListResponseDto(
                round = weeklyContest.round,
                subject = weeklyContest.subject,
                posts = postSlice.content.map {
                    WeeklyContestPostDto(
                        nickname = it.member.nickname!!,
                        profileImageUrl = it.member.profileImageUrl ?: DEFAULT_PROFILE_IMAGE_URL,
                        postId = it.id,
                        imageUrl = it.imageUrl,
                        ratio = it.ratio,
                        reportCount = 0L, // TODO : 신고 기능 추가 시 수정
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
