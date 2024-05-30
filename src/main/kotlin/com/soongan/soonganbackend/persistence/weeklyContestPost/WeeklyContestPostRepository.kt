package com.soongan.soonganbackend.persistence.weeklyContestPost

import org.springframework.data.jpa.repository.JpaRepository

interface WeeklyContestPostRepository: JpaRepository<WeeklyContestPostEntity, Long> {
}
