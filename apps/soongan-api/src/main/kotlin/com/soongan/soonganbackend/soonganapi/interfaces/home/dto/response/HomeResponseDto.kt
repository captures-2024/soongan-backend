package com.soongan.soonganbackend.soonganapi.interfaces.home.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.type.WeeklyContestPostAndIsLiked
import com.soongan.soonganbackend.soongansupport.domain.ContestStatusEnum
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "홈 화면 정보 응답 DTO")
data class HomeResponseDto(
    @Schema(description = "진행 중인 대회 정보", required = true)
    val contestInfo: ContestInfo,

    @Schema(description = "내가 작성한 게시글 정보", required = true)
    val postInfo: List<HomeMyPostInfo>
) {

    companion object {

        // daily contest response 와 분리하기 위한 네이밍
        fun fromWeeklyContest(
            weeklyContest: WeeklyContestEntity,
            status: ContestStatusEnum,
            postInfo: List<WeeklyContestPostAndIsLiked>
        ): HomeResponseDto {
            return HomeResponseDto(
                contestInfo = ContestInfo(
                    contestType = ContestTypeEnum.WEEKLY,
                    subject = weeklyContest.subject,
                    startAt = weeklyContest.startAt,
                    endAt = weeklyContest.endAt,
                    status = status
                ),
                postInfo = postInfo.map {
                    HomeMyPostInfo(
                        postId = it.post.id,
                        imageUrl = it.post.imageUrl,
                        likeCount = it.post.likeCount,
                        commentCount = it.post.commentCount,
                        isLiked = it.isLiked
                    )
                }
            )
        }
    }

    data class ContestInfo(
        val contestType: ContestTypeEnum,
        val subject: String,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
        @Schema(description = "콘테스트 상태", example = "UPCOMING, IN_PROGRESS, CLOSED")
        val status: ContestStatusEnum
    )

    data class HomeMyPostInfo(
        val postId: Long,
        val imageUrl: String,
        val likeCount: Int,
        val commentCount: Int,
        val isLiked: Boolean
    )
}
