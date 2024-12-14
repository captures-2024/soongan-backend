package com.soongan.soonganbackend.soonganapi.service.report

import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request.CreateReportRequestDto
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import org.springframework.stereotype.Service

@Service
class ReportService(
    private val reportAdapter: ReportAdapter,
    private val weeklyContestAdapter: WeeklyContestAdapter,
    private val commentAdapter: CommentAdapter
) {

    fun report(loginMember: MemberEntity, createReportRequestDto: CreateReportRequestDto) {
    }
}