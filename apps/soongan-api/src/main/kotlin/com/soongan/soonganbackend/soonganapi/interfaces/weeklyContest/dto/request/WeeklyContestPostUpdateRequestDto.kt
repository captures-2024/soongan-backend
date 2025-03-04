package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.request

import jakarta.validation.constraints.NotNull

data class WeeklyContestPostUpdateRequestDto(
    @field:NotNull
    val title: String
)