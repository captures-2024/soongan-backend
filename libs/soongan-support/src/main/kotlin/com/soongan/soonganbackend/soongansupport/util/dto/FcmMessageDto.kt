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
    val data: Map<String, String>
) {

    companion object {
        fun createCommentMessages(tokens: List<String>, postId: Long): List<Message> {
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
    }
}

data class Notification(
    val title: String,
    val body: String,
    val image: String? = null  // FCM 푸시 알림 이미지 (썸네일)
)