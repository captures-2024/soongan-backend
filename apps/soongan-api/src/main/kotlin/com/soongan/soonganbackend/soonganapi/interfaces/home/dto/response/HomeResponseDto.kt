package com.soongan.soonganbackend.soonganapi.interfaces.home.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.comment.ContestTypeEnum
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import java.time.LocalDateTime

data class HomeResponseDto(
    val contestInfo: ContestInfo,
    val postInfo: List<HomeMyPostInfo>
) {

    companion object {

        // daily contest response 와 분리하기 위한 네이밍
        fun fromWeeklyContest(
            weeklyContest: WeeklyContestEntity,
            postInfo: List<WeeklyContestPostEntity>
        ): HomeResponseDto {
            return HomeResponseDto(
                contestInfo = ContestInfo(
                    contestType = ContestTypeEnum.WEEKLY,
                    subject = weeklyContest.subject,
                    startAt = weeklyContest.startAt,
                    endAt = weeklyContest.endAt
                ),
                postInfo = postInfo.map {
                    HomeMyPostInfo(
                        postId = it.id!!,
                        imageUrl = it.imageUrl,
                        likeCount = it.likeCount,
                        commentCount = it.commentCount
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
    )

    data class HomeMyPostInfo(
        val postId: Long,
        val imageUrl: String,
        val likeCount: Int,
        val commentCount: Int
    )
}
