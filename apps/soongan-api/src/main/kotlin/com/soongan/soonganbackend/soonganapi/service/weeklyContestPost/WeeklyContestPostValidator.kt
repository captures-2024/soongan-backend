package com.soongan.soonganbackend.soonganapi.service.weeklyContestPost

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Component

@Component
class WeeklyContestPostValidator(
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter
) {

    // 최대 게시물 등록 개수 제한 검증
    fun validateMaxRegisterPost(
        weeklyContest: WeeklyContestEntity,
        member: MemberEntity
    ){
        val registeredPostCount = weeklyContestPostAdapter.countRegisteredPostByMember(weeklyContest, member)

        if (weeklyContest.maxPostAllowed <= registeredPostCount + 1) {
            throw (SoonganException(StatusCode.SOONGAN_API_WEEKLY_CONTEST_POST_REGISTER_LIMIT_EXCEEDED))
        }
    }
}
