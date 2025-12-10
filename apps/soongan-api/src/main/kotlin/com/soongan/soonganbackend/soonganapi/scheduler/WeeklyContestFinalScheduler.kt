package com.soongan.soonganbackend.soonganapi.scheduler

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal.WeeklyContestFinalAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal.WeeklyContestFinalEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class WeeklyContestFinalScheduler(
    private val weeklyContestAdapter: WeeklyContestAdapter,
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val weeklyContestFinalAdapter: WeeklyContestFinalAdapter
) {
    private val logger = LoggerFactory.getLogger(WeeklyContestFinalScheduler::class.java)

    /**
     * 매 시간마다 종료된 콘테스트의 최종 순위를 확정합니다.
     * - 종료된 콘테스트 중 Final 데이터가 없는 것을 찾아서
     * - 좋아요 수 기준 상위 7개 게시물을 Final 테이블에 저장합니다.
     */
    @Scheduled(cron = "0 0 * * * *") // 매 시간 정각에 실행
    fun finalizeWeeklyContestRankings() {
        logger.info("[WeeklyContestFinalScheduler] 콘테스트 최종 순위 확정 작업 시작")

        try {
            // 1. 종료된 콘테스트 조회
            val endedContests = weeklyContestAdapter.getEndedWeeklyContests()
            logger.info("[WeeklyContestFinalScheduler] 종료된 콘테스트 ${endedContests.size}개 조회됨")

            var processedCount = 0
            var skippedCount = 0

            // 2. 각 콘테스트에 대해 Final 데이터가 없으면 생성
            endedContests.forEach { contest ->
                // Final 데이터가 이미 있으면 스킵
                if (weeklyContestFinalAdapter.hasFinalData(contest.id)) {
                    skippedCount++
                    return@forEach
                }

                // 상위 7개 게시물 조회 (좋아요 수 기준, 동점 시 업로드 시간 빠른 순)
                val top7Posts = weeklyContestPostAdapter.getTop7Posts(contest)

                if (top7Posts.isEmpty()) {
                    logger.warn("[WeeklyContestFinalScheduler] 콘테스트 ${contest.id} (Round ${contest.round})에 게시물이 없어 스킵합니다.")
                    skippedCount++
                    return@forEach
                }

                // WeeklyContestFinalEntity 생성
                val finalEntities = top7Posts.mapIndexed { index, post ->
                    WeeklyContestFinalEntity(
                        weeklyContest = contest,
                        weeklyContestPost = post,
                        ranking = index + 1, // 1등부터 7등까지
                        score = post.likeCount
                    )
                }

                // 저장
                weeklyContestFinalAdapter.saveAll(finalEntities)
                processedCount++
                logger.info("[WeeklyContestFinalScheduler] 콘테스트 ${contest.id} (Round ${contest.round}) 최종 순위 확정 완료: ${finalEntities.size}개 게시물")
            }

            logger.info("[WeeklyContestFinalScheduler] 콘테스트 최종 순위 확정 작업 완료 - 처리: ${processedCount}개, 스킵: ${skippedCount}개")
        } catch (e: Exception) {
            logger.error("[WeeklyContestFinalScheduler] 콘테스트 최종 순위 확정 작업 중 오류 발생", e)
        }
    }
}