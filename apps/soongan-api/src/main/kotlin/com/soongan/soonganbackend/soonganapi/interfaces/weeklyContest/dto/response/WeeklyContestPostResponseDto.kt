package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주간 콘테스트 게시글 조회 응답 DTO")
data class WeeklyContestPostResponseDto(
    @Schema(description = "조회 요청한 회원의 ID", type = "Long", nullable = true)
    val memberId: Long?, // 이거 왜 게시글 작성자가 아니라 조회 요청한 회원의 ID인지?

    @Schema(description = "게시글 작성자 회원 ID", type = "Long", nullable = true)
    val authorMemberId: Long,

    @Schema(description = "게시글 ID", type = "Long")
    val postId: Long,

    @Schema(description = "게시글 제목", type = "String")
    val title: String,

    @Schema(description = "게시글 이미지 URL", type = "String")
    val imageUrl: String,

    @Schema(description = "이미지 가로세로 비율 (width / height)", type = "Double", nullable = true)
    val ratio: Double?,

    @Schema(description = "게시글 작성자 닉네임", type = "String")
    val nickname: String,

    @Schema(description = "게시글 좋아요 수", type = "Int")
    val likeCount: Int,

    @Schema(description = "조회 요청한 회원이 해당 게시글을 좋아요 했는지 여부", type = "Boolean")
    val isLiked: Boolean,

    @Schema(description = "게시글 댓글 수", type = "Int")
    val commentCount: Int,

    @Schema(description = "해당 게시글이 탑 7에 속하는지 여부. 아직 진행 중인 콘테스트라면 무조건 false", type = "Boolean")
    val isTop7: Boolean,

    @Schema(description = "콘테스트 회차", type = "Int")
    val weeklyContestRound: Int,

    @Schema(description = "콘테스트 주제", type = "String")
    val weeklyContestSubject: String,

    @Schema(description = "신고 횟수", type = "Long")
    val reportCount: Long = 0L
) {

    companion object {
        fun from(
            memberId: Long? = null,
            weeklyContestPost: WeeklyContestPostEntity,
            weeklyContest: WeeklyContestEntity,
            isLiked: Boolean = false,
            isTop7: Boolean = false
        ): WeeklyContestPostResponseDto {
            val member = weeklyContestPost.member
            return WeeklyContestPostResponseDto(
                memberId = memberId,
                authorMemberId = member?.id ?: 0L,
                postId = weeklyContestPost.id,
                title = weeklyContestPost.title,
                imageUrl = weeklyContestPost.imageUrl,
                ratio = weeklyContestPost.ratio,
                nickname = member?.nickname ?: "알 수 없음",
                likeCount = weeklyContestPost.likeCount,
                isLiked = isLiked,
                commentCount = weeklyContestPost.commentCount,
                isTop7 = isTop7,
                weeklyContestRound = weeklyContest.round,
                weeklyContestSubject = weeklyContest.subject,
                reportCount = 0L // TODO : 신고 기능 추가 시 수정
            )
        }
    }
}
