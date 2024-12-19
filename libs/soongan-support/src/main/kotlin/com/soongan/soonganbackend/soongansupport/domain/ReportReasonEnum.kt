package com.soongan.soonganbackend.soongansupport.domain

enum class ReportReasonEnum(
    val message: String
) {
    INAPPROPRIATE_PHOTO_OR_BEHAVIOR("부적절한 사진 게시 및 언행"),
    PROFANITY_HATE_SPEECH("욕설, 혐오, 비하 등이 포함된 표현"),
    COPYRIGHT_OR_PRIVACY_VIOLATION("도용, 초상권, 저작권 등 타인의 권리 침해"),
    SPAM("도배"),
    PROMOTIONAL_CONTENT("홍보용 사진 혹은 댓글 게시"),
    OTHER("기타")
}