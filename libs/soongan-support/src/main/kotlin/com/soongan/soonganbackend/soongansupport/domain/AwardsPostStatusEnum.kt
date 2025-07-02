package com.soongan.soonganbackend.soongansupport.domain

enum class AwardsPostStatusEnum {
    ACTIVE, // 활성화된 게시글
    BLINDED, // 블라인드 처리된 게시글
    DELETED_BY_ADMIN, // 관리자에 의해 삭제된 게시글
    DELETED_BY_CREATOR // 작성자가 삭제한 게시글
}