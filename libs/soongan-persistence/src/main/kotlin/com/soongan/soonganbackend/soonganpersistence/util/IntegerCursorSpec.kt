package com.soongan.soonganbackend.soonganpersistence.util

import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.core.types.dsl.StringExpressions
import org.springframework.stereotype.Component

@Component
class IntegerCursorSpec: CursorSpec<NumberPath<Int>, Int> {

    override fun generateCursor(sortCriteria: Int, pk: Long): String {
        val intCursorPart = sortCriteria.toString().padStart(10, '0')
        val pkCursorPart = pk.toString().padStart(10, '0')

        return intCursorPart + pkCursorPart
    }

    override fun generateCursorExpression(
        sortCriteria: NumberPath<Int>,
        pk: StringExpression
    ): StringExpression {
        return StringExpressions.lpad(sortCriteria.stringValue(), 10, '0')
            .concat(StringExpressions.lpad(pk, 10, '0'))
    }


}