package com.soongan.soonganbackend.soongansupport.util.dto

data class CursorResponseWrapper<T> (
    val content: List<T>,
    val hasNext: Boolean,
    val lastCursor: String?,
) {

    fun <K> map(transform: (T) -> K): CursorResponseWrapper<K> {
        return CursorResponseWrapper(
            content = content.map { transform(it) },
            hasNext = hasNext,
            lastCursor = lastCursor
        )
    }
}