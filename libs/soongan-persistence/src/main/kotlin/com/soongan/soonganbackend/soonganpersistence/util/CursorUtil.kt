package com.soongan.soonganbackend.soonganpersistence.util

import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.jpa.impl.JPAQuery
import com.soongan.soonganbackend.soongansupport.util.common.SortDirection


fun <T> JPAQuery<T>.applySortCondition(
    cursorExpression: StringExpression,
    currentCursor: String?,
    sortDirection: SortDirection
): JPAQuery<T> {
    currentCursor?.let {
        when (sortDirection) {
            SortDirection.ASC -> this.where(cursorExpression.gt(it))
            SortDirection.DESC -> this.where(cursorExpression.lt(it))
        }
    }
    return this
}