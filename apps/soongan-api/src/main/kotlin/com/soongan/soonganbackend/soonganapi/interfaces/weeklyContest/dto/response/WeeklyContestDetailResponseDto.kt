package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal.WeeklyContestFinalEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "역대 주간 콘테스트 상세 조회 응답 DTO")
data class WeeklyContestDetailResponseDto(
    val postsCount: Int,
    val top7Posts: List<TopPostResponseDto>
) {
    @Schema(description = "역대 주간 콘테스트 상위 게시글 응답 DTO")
    data class TopPostResponseDto(
        val id: Long,
        val postId: Long,
        val nickname: String?,
        val ranking: Int?,
        val score: Int,
    ) {

        companion object {
            fun from(weeklyContestFinalEntity: WeeklyContestFinalEntity): TopPostResponseDto {
                return TopPostResponseDto(
                    id = weeklyContestFinalEntity.id!!,
                    postId = weeklyContestFinalEntity.weeklyContestPost.id!!,
                    nickname = weeklyContestFinalEntity.weeklyContestPost.member.nickname,
                    ranking = weeklyContestFinalEntity.ranking,
                    score = weeklyContestFinalEntity.score
                )
            }
        }
    }

    companion object {
        fun from(postsCount: Int, top7Posts: List<WeeklyContestFinalEntity>): WeeklyContestDetailResponseDto {
            return WeeklyContestDetailResponseDto(
                postsCount = postsCount,
                top7Posts = top7Posts.map { TopPostResponseDto.from(it) }
            )
        }
    }
}
