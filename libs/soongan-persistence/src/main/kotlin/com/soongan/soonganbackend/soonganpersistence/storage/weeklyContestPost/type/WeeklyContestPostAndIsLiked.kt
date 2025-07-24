package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.type

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity

data class WeeklyContestPostAndIsLiked(
    val post: WeeklyContestPostEntity,
    val isLiked: Boolean,
)
