package com.soongan.soonganbackend.soongansupport.util.dto

import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class FcmMessageDto(
    val message: Message
)

data class Message(
    val token: String,
    val notification: Notification,
    val data: MessageData
) {

    companion object {
        fun createCommentMessage(token: String, postId: Long): Message {
            return Message(
                token = token,
                notification = Notification(
                    title = "회원님의 작품에 누군가 댓글을 남겼어요~",
                    body = "지금 바로 확인해 보세요!"
                ),
                data = MessageData(
                    link = "/weekly-contest/${postId}",  // TODO: 앱 딥링크 논의
                    notificationType = NotificationTypeEnum.ACTIVITY,
                    postId = postId
                )
            )
        }
    }
}

data class Notification(
    val title: String,
    val body: String,
    val image: String? = null  // FCM 푸시 알림 이미지 (썸네일)
)

data class MessageData(
    val link: String,  // 알림 클릭 시 이동할 링크
    val notificationType: NotificationTypeEnum,
    val postId: Long? = null,  // 게시글 관련 알림일 시 게시글 ID
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)  // 알림 발생 시간 (ISO 8601 문자열)
)