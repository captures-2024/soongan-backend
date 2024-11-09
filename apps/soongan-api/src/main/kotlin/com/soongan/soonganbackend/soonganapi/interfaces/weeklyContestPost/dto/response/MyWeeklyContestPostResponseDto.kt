package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.util.dto.PageDto
import org.springframework.data.domain.Page

data class MyWeeklyContestPostResponseDto(
    val postInfo: List<PostInfo>,
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
                        postId = it.id!!,
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
