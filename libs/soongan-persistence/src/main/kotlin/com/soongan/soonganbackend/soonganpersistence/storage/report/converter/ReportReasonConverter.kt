package com.soongan.soonganbackend.soonganpersistence.storage.report.converter

import com.soongan.soonganbackend.soongansupport.domain.ReportReasonEnum
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

// JPA에서 ReportReasonEnum을 다룰 때 자동으로 enum을 message로, message를 enum으로 변환해주는 컨버터
@Converter(autoApply = true)
class ReportReasonConverter : AttributeConverter<ReportReasonEnum, String> {

    override fun convertToDatabaseColumn(attribute: ReportReasonEnum?): String? {
        return attribute?.message
    }

    override fun convertToEntityAttribute(dbData: String?): ReportReasonEnum? {
        return ReportReasonEnum.entries.find { it.message == dbData }
    }
}