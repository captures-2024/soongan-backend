package com.soongan.soonganbackend.soonganpersistence.util

import com.querydsl.core.types.ConstantImpl
import com.querydsl.core.types.dsl.DateTimePath
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.core.types.dsl.StringExpressions
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class DateTimeCursorSpec: CursorSpec<DateTimePath<LocalDateTime>, LocalDateTime> {

    override fun generateCursor(sortCriteria: LocalDateTime, pk: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val dateTimeCursorPart = sortCriteria.format(formatter).padStart(20, '0')
        val pkCursorPart = pk.toString().padStart(10, '0')

        return dateTimeCursorPart + pkCursorPart
    }

    override fun generateCursorExpression(
        sortCriteria: DateTimePath<LocalDateTime>,
        pk: StringExpression
    ): StringExpression {
        val dateTimeExpression = Expressions.stringTemplate(
            "datetime_cursor({0}, {1})",
            sortCriteria.stringValue(),
            ConstantImpl.create("%Y%m%d%H%i%s")
        )

        return StringExpressions.lpad(dateTimeExpression, 20, '0')
            .concat(StringExpressions.lpad(pk, 10, '0'))
    }
}