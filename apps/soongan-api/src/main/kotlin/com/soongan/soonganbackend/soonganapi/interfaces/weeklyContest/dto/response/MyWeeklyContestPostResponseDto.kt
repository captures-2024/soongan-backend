package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response


import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.PostInfo
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.util.dto.PageDto
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

@Schema(description = "내가 작성한 주간 대회 게시글 응답 DTO")
data class MyWeeklyContestPostResponseDto(
    @Schema(description = "내가 작성한 게시글 정보", required = true)
    val postInfo: List<PostInfo>,

    @Schema(description = "페이지네이션 정보", required = true)
    val pageInfo: PageDto
) {

    companion object {
        fun from(
            postPage: Page<WeeklyContestPostEntity>
        ): MyWeeklyContestPostResponseDto {
            return MyWeeklyContestPostResponseDto(
                postInfo = postPage.content.map {
                    PostInfo(
                        round = it.weeklyContest.round,
                        subject = it.weeklyContest.subject,
                        postId = it.id,
                        imageUrl = it.imageUrl,
                        likeCount = it.likeCount,
                        reportCount = 0L, // TODO : 신고 기능 추가 시 수정
                    )
                },
                pageInfo = PageDto(
                    page = postPage.number,
                    size = postPage.size,
                    hasNext = postPage.hasNext()
                )
            )
        }
    }
}
