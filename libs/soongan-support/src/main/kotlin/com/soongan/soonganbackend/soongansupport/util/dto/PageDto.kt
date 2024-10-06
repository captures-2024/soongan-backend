package com.soongan.soonganbackend.soongansupport.util.dto

data class PageDto(
    val page: Int,
    val size: Int,
    val hasNext: Boolean? = null,
)
