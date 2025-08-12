package com.soongan.soonganbackend.soonganpersistence.util

import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.StringExpression

interface CursorSpec<T> {

    /**
     * @param pk 정렬의 중복을 피하기 위해 사용되는 PK는 항상 맨 마지막 인자로 전달되어야 한다.
     */
    fun generateCursor(sortCriteria: T, pk: Long): String

    fun generateCursorExpression(sortCriteria: Path<T>, pk: Path<Long>): StringExpression

}