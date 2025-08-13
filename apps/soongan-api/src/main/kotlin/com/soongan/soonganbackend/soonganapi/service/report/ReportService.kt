package com.soongan.soonganbackend.soonganapi.service.report

import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request.ExplainSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request.ReportSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.response.ReportSaveResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.explain.ExplainAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.explain.ExplainEntity
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationEntity
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soonganredis.constant.RedisStreamKey
import com.soongan.soonganbackend.soonganredis.producer.RedisMessageProducer
import com.soongan.soonganbackend.soongansupport.domain.NotificationSubTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ReportTypeEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import com.soongan.soonganbackend.soongansupport.util.noti.createBlockMessages
import com.soongan.soonganbackend.soongansupport.util.noti.createNeedExplainMessages
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReportService(
    private val reportAdapter: ReportAdapter,
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val commentAdapter: CommentAdapter,
    private val explainAdapter: ExplainAdapter,
    private val fcmTokenAdapter: FcmTokenAdapter,
    private val notificationAdapter: NotificationAdapter,
    private val redisMessageProducer: RedisMessageProducer,
) {
    private val BLIND_REPORT_COUNT = 3

    fun report(loginMember: MemberEntity, dto: ReportSaveRequestDto): ReportSaveResponseDto {
        val target = getTargetEntity(dto.targetType, dto.targetId)
        val targetMember = getTargetMember(target)

        val savedReport = reportAdapter.save(
            ReportEntity(
                reportMember = loginMember,
                targetMember = targetMember,
                targetId = dto.targetId,
                targetType = dto.targetType,
                reportType = dto.reportType,
                reason = dto.reason,
            )
        )

        handleBlindingIfNeeded(dto.targetId, dto.targetType, target)

        val reportHistories = reportAdapter.getReportHistoriesByReportMember(loginMember)

        // 도용, 초상권, 저작권 등 타인의 권리 침해인 경우 소명 요청
        if (dto.reportType == ReportTypeEnum.COPYRIGHT_OR_PRIVACY_VIOLATION) {
            val tokens = fcmTokenAdapter.findAllByMemberId(targetMember.id)
            val messages = createNeedExplainMessages(
                tokens = tokens.map { it.token },
                targetId = dto.targetId,
                targetType = dto.targetType
            )
            if (messages.isEmpty()) return ReportSaveResponseDto.from(savedReport, reportHistories)

            // 알림 전송
            redisMessageProducer.addMessage(RedisStreamKey.SOONGAN_NOTI, messages)

            // 알림센터에 저장
            notificationAdapter.save(
                NotificationEntity(
                    member = targetMember,
                    type = NotificationTypeEnum.ACTIVITY,
                    subType = NotificationSubTypeEnum.EXPLAIN,
                    title = messages.first().notification.title,
                    body = messages.first().notification.body,
                )
            )
        }

        return ReportSaveResponseDto.from(savedReport, reportHistories)
    }

    fun explain(loginMember: MemberEntity, explainSaveRequestDto: ExplainSaveRequestDto) {
        explainAdapter.save(
            ExplainEntity(
                member = loginMember,
                targetId = explainSaveRequestDto.targetId,
                targetType = explainSaveRequestDto.targetType,
                explains = explainSaveRequestDto.explain
            )
        )
    }

    private fun getTargetEntity(type: ReportTargetTypeEnum, id: Long): Any {
        return when (type) {
            ReportTargetTypeEnum.WEEKLY_POST ->
                weeklyContestPostAdapter.getByIdOrNull(id)
                    ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)

            ReportTargetTypeEnum.COMMENT ->
                commentAdapter.getByIdOrNull(id)
                    ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_COMMENT)

            ReportTargetTypeEnum.DAILY_POST ->
                throw SoonganException(StatusCode.SOONGAN_API_INVALID_REQUEST, "일간 콘테스트 게시글은 아직 지원하지 않습니다.")
        }
    }

    private fun getTargetMember(target: Any): MemberEntity {
        return when (target) {
            is WeeklyContestPostEntity -> target.member
            is CommentEntity -> target.member
            else -> throw SoonganException(StatusCode.SOONGAN_API_INVALID_REQUEST, "지원하지 않는 대상입니다.")
        }
    }

    private fun handleBlindingIfNeeded(targetId: Long, targetType: ReportTargetTypeEnum, target: Any) {
        val reportCount = reportAdapter.countByTargetIdAndTargetType(targetId, targetType)
        if (reportCount < BLIND_REPORT_COUNT) return

        val now = LocalDateTime.now()
        when (target) {
            is WeeklyContestPostEntity -> {
                if (target.blindedAt == null) {
                    weeklyContestPostAdapter.save(target.copy(blindedAt = now))
                    sendAndSaveNoti(
                        targetMember = target.member,
                        targetId = targetId,
                        targetType = targetType
                    )
                }
            }

            is CommentEntity -> {
                if (target.blindedAt == null) {
                    commentAdapter.save(target.copy(blindedAt = now))
                    sendAndSaveNoti(
                        targetMember = target.member,
                        targetId = targetId,
                        targetType = targetType
                    )
                }
            }
        }
    }

    private fun sendAndSaveNoti(
        targetMember: MemberEntity,
        targetId: Long,
        targetType: ReportTargetTypeEnum
    ) {
        val tokens = fcmTokenAdapter.findAllByMemberId(targetMember.id)
        val messages = createBlockMessages(
            tokens = tokens.map { it.token },
            targetId = targetId,
            targetType = targetType
        )
        if (messages.isEmpty()) return

        // 알림 전송
        redisMessageProducer.addMessage(RedisStreamKey.SOONGAN_NOTI, messages)

        // 알림센터에 저장
        notificationAdapter.save(
            NotificationEntity(
                member = targetMember,
                type = NotificationTypeEnum.ACTIVITY,
                subType = NotificationSubTypeEnum.BLOCK,
                title = messages.first().notification.title,
                body = messages.first().notification.body,
            )
        )
    }
}