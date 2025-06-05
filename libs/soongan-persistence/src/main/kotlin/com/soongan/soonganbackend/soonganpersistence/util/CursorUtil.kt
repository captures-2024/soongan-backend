package com.soongan.soonganbackend.soonganpersistence.util

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.jpa.impl.JPAQuery
import com.soongan.soonganbackend.soongansupport.util.common.SortDirection
import kotlin.math.pow


fun generateCursor(vararg fields: Any): StringExpression {
    val cursorParts = fields.indices.joinToString("") { index ->
        "LPAD(POW(10, 10) - {$index}, 10, '0')"
    }

    val template = "CONCAT($cursorParts)"
    return Expressions.stringTemplate(template, *fields)
}


fun <T> calculateNextCursor(
    collection: Iterable<T>,
    fieldExtractor: (T) -> List<Number>
): String? {
    return collection.lastOrNull()?.let { item ->
        val fields = fieldExtractor(item)
        val formattedFields = fields.map { field ->
            String.format("%010d", 10.0.pow(10.0).toLong() - field.toLong())
        }
        formattedFields.joinToString(separator = "")
    }
}

fun <T> JPAQuery<T>.applySortCondition(
    cursorExpression: StringExpression,
    currentCursor: String?,
    sortDirection: SortDirection
): JPAQuery<T> {
    currentCursor?.let {
        when (sortDirection) {
            SortDirection.ASC -> this.where(cursorExpression.lt(it))
            SortDirection.DESC -> this.where(cursorExpression.gt(it))
        }
    }
    return this
}