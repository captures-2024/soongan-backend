package com.soongan.soonganbackend.soonganapi.interfaces.awards.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal.WeeklyContestFinalEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "역대 주간 콘테스트 상세 조회 응답 DTO")
data class AwardsDetailResponseDto(
    val postsCount: Int,
    val firstPrizePost: FirstPrizePostResponseDto,
    val otherTop7Posts: List<TopPostResponseDto>
) {
    @Schema(description = "역대 주간 콘테스트 1위 게시글 응답 DTO")
    data class FirstPrizePostResponseDto(
        val postId: Long,
        val title: String,
        val imageUrl: String,
        val nickname: String?,
        val score: Int,
    ) {
        companion object {
            fun from(weeklyContestFinalEntity: WeeklyContestFinalEntity): FirstPrizePostResponseDto {
                return FirstPrizePostResponseDto(
                    postId = weeklyContestFinalEntity.weeklyContestPost.id!!,
                    title = weeklyContestFinalEntity.weeklyContestPost.title,
                    imageUrl = weeklyContestFinalEntity.weeklyContestPost.imageUrl,
                    nickname = weeklyContestFinalEntity.weeklyContestPost.member.nickname,
                    score = weeklyContestFinalEntity.score
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
    ) {

        companion object {
            fun from(weeklyContestFinalEntity: WeeklyContestFinalEntity): TopPostResponseDto {
                return TopPostResponseDto(
                    postId = weeklyContestFinalEntity.weeklyContestPost.id!!,
                    imageUrl = weeklyContestFinalEntity.weeklyContestPost.imageUrl,
                    nickname = weeklyContestFinalEntity.weeklyContestPost.member.nickname,
                    ranking = weeklyContestFinalEntity.ranking,
                    score = weeklyContestFinalEntity.score
                )
            }
        }
    }

    companion object {
        fun from(postsCount: Int, top7Posts: List<WeeklyContestFinalEntity>): AwardsDetailResponseDto {
            if (top7Posts.isEmpty()) {
                throw IllegalArgumentException("Top posts list cannot be empty")
            }
            val sortedTop7Posts = top7Posts.sortedBy { it.ranking }
            val firstPrizePost = FirstPrizePostResponseDto.from(sortedTop7Posts.first())
            val otherTop7Posts = sortedTop7Posts.drop(1).map { TopPostResponseDto.from(it) }
            return AwardsDetailResponseDto(
                postsCount = postsCount,
                firstPrizePost = firstPrizePost,
                otherTop7Posts = otherTop7Posts
            )
        }
    }
}