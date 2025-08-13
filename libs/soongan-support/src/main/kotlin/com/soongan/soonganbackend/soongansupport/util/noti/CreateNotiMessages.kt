package com.soongan.soonganbackend.soongansupport.util.noti

import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import com.soongan.soonganbackend.soongansupport.util.dto.Message
import com.soongan.soonganbackend.soongansupport.util.dto.Notification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 누군가가 댓글을 남겼을 때
fun createCommentNotiMessages(tokens: List<String>, postId: Long): List<Message> {
    return tokens.map { token ->
        Message(
            token = token,
            notification = Notification(
                title = "회원님의 작품에 누군가 댓글을 남겼어요~",
                body = "지금 바로 확인해 보세요!"
            ),
            data = mapOf(
                "link" to "/post/$postId",
                "notificationType" to NotificationTypeEnum.ACTIVITY.toString(),
                "postId" to postId.toString(),
                "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        )
    }
}

// 내 작품이 신고받아서 소명이 필요한 경우
fun createNeedExplainMessages(tokens: List<String>, postId: Long): List<Message> {
    return tokens.map { token ->
        Message(
            token = token,
            notification = Notification(
                title = "[필수] 신고 접수로 인한 소명 절차 진행",
                body = "신고가 접수돼 소명이 필요합니다. 소명 절차를 진행해 주세요."
            ),
            data = mapOf(
                "link" to "/post/$postId",
                "notificationType" to NotificationTypeEnum.ACTIVITY.toString(),
                "postId" to postId.toString(),
                "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        )
    }
}

// 내 작품이 3회 이상 신고받아서 숨김 처리된 경우
fun createBlockPostMessages(tokens: List<String>, postId: Long): List<Message> {
    return tokens.map { token ->
        Message(
            token = token,
            notification = Notification(
                title = "신고 누적으로 인한 게시물 숨김 처리 진행",
                body = "해당 작품은 모두에게 숨겨집니다. 클릭해 확인해 주세요."
            ),
            data = mapOf(
                "link" to "/post/$postId",
                "notificationType" to NotificationTypeEnum.ACTIVITY.toString(),
                "postId" to postId.toString(),
                "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        )
    }
}

// 내가 신고한 내역의 결과가 도착한 경우
fun createReportResultMessages(tokens: List<String>, postId: Long, isAccepted: Boolean, reportDate: LocalDateTime): List<Message> {
    val body = if (isAccepted) {
        "[${reportDate.format(DateTimeFormatter.ofPattern("MM.dd"))}] 신고가 받아들여져 정상적으로 처리되었습니다. 클릭해 더 자세히 알아보세요."
    } else {
        "[${reportDate.format(DateTimeFormatter.ofPattern("MM.dd"))}] 신고가 받아들여지지 않았습니다. 클릭해 더 자세히 알아보세요."
    }
    return tokens.map { token ->
        Message(
            token = token,
            notification = Notification(
                title = "신고 결과 안내",
                body = body
            ),
            data = mapOf(
                "link" to "/post/$postId",
                "notificationType" to NotificationTypeEnum.ACTIVITY.toString(),
                "postId" to postId.toString(),
                "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        )
    }
}