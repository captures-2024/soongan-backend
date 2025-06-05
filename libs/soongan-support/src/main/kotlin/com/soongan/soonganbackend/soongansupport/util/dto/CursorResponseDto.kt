package com.soongan.soonganbackend.soongansupport.util.dto

data class CursorResponseDto<T>(
    val nextCursor: String?,
    val data: T?
) {

    companion object {
        fun <T> from(nextCursor: String?,  data: T?): CursorResponseDto<T> {
            return CursorResponseDto(nextCursor, data)
        }
    }
}