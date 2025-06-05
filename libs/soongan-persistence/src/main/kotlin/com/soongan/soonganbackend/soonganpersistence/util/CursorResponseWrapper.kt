package com.soongan.soonganbackend.soonganpersistence.util

data class CursorResponseWrapper<T>(
    val nextCursor: String?,
    val data: T?
) {

    companion object {
        fun <T> from(nextCursor: String?,  data: T?): CursorResponseWrapper<T> {
            return CursorResponseWrapper(nextCursor, data)
        }
    }
}