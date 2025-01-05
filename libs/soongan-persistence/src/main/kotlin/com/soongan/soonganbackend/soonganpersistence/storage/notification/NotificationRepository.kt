package com.soongan.soonganbackend.soonganpersistence.storage.notification

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NotificationRepository: JpaRepository<NotificationEntity, Long> {

    fun findAllByMemberAndType(member: MemberEntity, type: NotificationTypeEnum): List<NotificationEntity>

    @Query(
        "SELECT n.type, COUNT(n) AS NotificationCount" +
            " FROM NotificationEntity n " +
            " WHERE n.member = :member " +
            " AND n.isRead = false "
    )
    fun countUnreadNotifications(member: MemberEntity): List<NotificationCountSummary>
}
