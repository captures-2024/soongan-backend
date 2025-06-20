package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response

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
    data class PostInfo(
        val round: Int,
        val subject: String,
        val postId: Long,
        val imageUrl: String,
        val likeCount: Int
    )

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
                        likeCount = it.likeCount
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
