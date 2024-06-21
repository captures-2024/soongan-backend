package com.soongan.soonganbackend.util.common.dto

data class PageDto(
    val page: Int,
    val size: Int,
    val hasNext: Boolean? = null,
)
