package com.soongan.soonganbackend.soonganapi.service.home

import com.soongan.soonganbackend.soonganapi.interfaces.home.dto.response.HomeResponseDto
import com.soongan.soonganbackend.soonganapi.service.weeklyContest.validator.WeeklyContestValidator
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.postLike.PostLikeAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import org.springframework.stereotype.Service

@Service
class HomeService(
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val weeklyContestValidator: WeeklyContestValidator,
    private val postLikeAdapter: PostLikeAdapter
) {

    fun getHome(loginMember: MemberEntity?): HomeResponseDto {
        val weeklyContest: WeeklyContestEntity = weeklyContestValidator.getWeeklyContestIfValidRound()

        if (loginMember == null) {
            return HomeResponseDto.fromWeeklyContest(weeklyContest, emptyList())
        }

        val homeWeeklyContestPostList: List<WeeklyContestPostEntity> =
            weeklyContestPostAdapter.getAllWeeklyContestPostByMemberAndWeeklyContest(
                loginMember,
                weeklyContest
            )
        val postList = homeWeeklyContestPostList.map { post ->
            val isLiked = postLikeAdapter.existsByPostIdAndContestTypeAndMember(
                post.id,
                ContestTypeEnum.WEEKLY,
                loginMember
            )
            Pair(post, isLiked)
        }

        return HomeResponseDto.fromWeeklyContest(weeklyContest, postList)
    }
}
