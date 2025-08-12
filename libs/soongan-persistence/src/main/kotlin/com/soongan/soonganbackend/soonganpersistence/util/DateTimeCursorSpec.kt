package com.soongan.soonganbackend.soonganpersistence.util

import com.querydsl.core.types.ConstantImpl
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.Expressions

import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.core.types.dsl.StringExpressions
import com.querydsl.core.types.dsl.StringTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class DateTimeCursorSpec: CursorSpec<LocalDateTime> {

    override fun generateCursor(sortCriteria: LocalDateTime, pk: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val dateTimeCursorPart = sortCriteria.format(formatter).padStart(20, '0')
        val pkCursorPart = pk.toString().padStart(10, '0')

        return dateTimeCursorPart + pkCursorPart
    }

    override fun generateCursorExpression(
        sortCriteria: Path<LocalDateTime>,
        pk: Path<Long>
    ): StringExpression {
        val dateTimeExpression = Expressions.stringTemplate(
            "DATE_FORMAT({0}, {1})",
            sortCriteria,
            ConstantImpl.create("%Y%m%d%H%i%s")
        )

        val pkExpression: StringTemplate = Expressions.stringTemplate(
            pk.toString()
        )

        return StringExpressions.lpad(dateTimeExpression, 20, '0')
            .concat(StringExpressions.lpad(pkExpression, 10, '0'))
    }
}