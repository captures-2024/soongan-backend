package com.soongan.soonganbackend.soonganapi.interfaces.awards.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal.WeeklyContestFinalEntity
import com.soongan.soonganbackend.soongansupport.domain.AwardsPostStatusEnum
import com.soongan.soonganbackend.soongansupport.domain.DeletedReasonEnum
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "역대 주간 콘테스트 상세 조회 응답 DTO")
data class AwardsDetailResponseDto(
    @Schema(required = true, description = "주간 콘테스트 주제")
    val subject: String,

    @Schema(required = true, description = "주간 콘테스트 라운드")
    val round: Int,

    @Schema(required = true, description = "주간 콘테스트 시작 일자")
    val startAt: LocalDateTime,

    @Schema(required = true, description = "주간 콘테스트 1차 투표 종료 일자")
    val endAt: LocalDateTime,

    @Schema(required = true, description = "해당 주간 콘테스트에 등록된 게시글 수")
    val postsCount: Int,

    @Schema(description = "해당 주간 콘테스트 1위 게시글 정보")
    val firstPrizePost: FirstPrizePostResponseDto,

    @Schema(description = "해당 주간 콘테스트 2위 ~ 7위 게시글 정보")
    val otherTop7Posts: List<TopPostResponseDto>
) {
    @Schema(description = "역대 주간 콘테스트 1위 게시글 응답 DTO")
    data class FirstPrizePostResponseDto(
        val postId: Long,
        val title: String,
        val imageUrl: String,
        val nickname: String?,
        val score: Int,
        val status: AwardsPostStatusEnum
    ) {
        companion object {
            fun from(weeklyContestFinalEntity: WeeklyContestFinalEntity): FirstPrizePostResponseDto {
                val post = weeklyContestFinalEntity.weeklyContestPost
                return FirstPrizePostResponseDto(
                    postId = post.id,
                    title = post.title,
                    imageUrl = post.imageUrl,
                    nickname = post.member?.nickname ?: "알 수 없음",
                    score = weeklyContestFinalEntity.score,
                    status = if (post.deletedAt != null) {
                        when (post.deletedReason) {
                            DeletedReasonEnum.BY_CREATOR -> AwardsPostStatusEnum.DELETED_BY_CREATOR
                            else -> AwardsPostStatusEnum.DELETED_BY_ADMIN // deletedAt이 true인데 deletedReason이 null인 경우는 어쩌지? 일단 admin 삭제로 처리
                        }
                    } else if (post.blindedAt != null) {
                        AwardsPostStatusEnum.BLINDED
                    } else {
                        AwardsPostStatusEnum.ACTIVE
                    }
                )
            }
        }
    }

    @Schema(description = "역대 주간 콘테스트 상위 게시글 응답 DTO")
    data class TopPostResponseDto(
        val postId: Long,
        val imageUrl: String,
        val nickname: String?,
        val ranking: Int,
        val score: Int,
        val status: AwardsPostStatusEnum
    ) {

        companion object {
            fun from(weeklyContestFinalEntity: WeeklyContestFinalEntity): TopPostResponseDto {
                val post = weeklyContestFinalEntity.weeklyContestPost
                val status = if (post.deletedAt != null) {
                    when (post.deletedReason) {
                        DeletedReasonEnum.BY_CREATOR -> AwardsPostStatusEnum.DELETED_BY_CREATOR
                        else -> AwardsPostStatusEnum.DELETED_BY_ADMIN
                    }
                } else if (post.blindedAt != null) {
                    AwardsPostStatusEnum.BLINDED
                } else {
                    AwardsPostStatusEnum.ACTIVE
                }

                return TopPostResponseDto(
                    postId = post.id,
                    imageUrl = post.imageUrl,
                    nickname = post.member?.nickname ?: "알 수 없음",
                    ranking = weeklyContestFinalEntity.ranking,
                    score = weeklyContestFinalEntity.score,
                    status = status
                )
            }
        }
    }

    companion object {
        fun from(weeklyContest: WeeklyContestEntity, postsCount: Int, top7Posts: List<WeeklyContestFinalEntity>): AwardsDetailResponseDto {
            if (top7Posts.isEmpty()) {
                throw IllegalArgumentException("Top posts list cannot be empty")
            }
            val sortedTop7Posts = top7Posts.sortedBy { it.ranking }
            val firstPrizePost = FirstPrizePostResponseDto.from(sortedTop7Posts.first())
            val otherTop7Posts = sortedTop7Posts.drop(1).map { TopPostResponseDto.from(it) }
            return AwardsDetailResponseDto(
                subject = weeklyContest.subject,
                round = weeklyContest.round,
                startAt = weeklyContest.startAt,
                endAt = weeklyContest.endAt,
                postsCount = postsCount,
                firstPrizePost = firstPrizePost,
                otherTop7Posts = otherTop7Posts
            )
        }
    }
}