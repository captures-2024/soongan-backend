package com.soongan.soonganbackend.soonganapi.interfaces.commentLike

import com.soongan.soonganbackend.soonganapi.interfaces.commentLike.dto.request.CommentLikeRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.commentLike.dto.response.CommentLikeResponseDto
import com.soongan.soonganbackend.soonganapi.service.commentLike.CommentLikeService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(Uri.COMMENTS + Uri.LIKE)
@RestController
@Tag(name = "Comment Like Apis", description = "댓글 좋아요 관련 API")
class CommentLikeController(
    private val commentLikeService: CommentLikeService
) {

    @PutMapping
    @Operation(summary = "댓글 좋아요 Api", description = "댓글에 좋아요를 추가합니다.")
    fun addLikeComment(
        @LoginMember loginMember: MemberEntity,
        @RequestBody request: CommentLikeRequestDto
    ): CommentLikeResponseDto {
        return commentLikeService.addLike(loginMember, request)
    }

    @DeleteMapping
    @Operation(summary = "댓글 좋아요 취소 Api", description = "댓글에 좋아요를 취소합니다.")
    fun cancleLikeComment(
        @LoginMember loginMember: MemberEntity,
        @RequestBody request: CommentLikeRequestDto
    ): CommentLikeResponseDto {
        return commentLikeService.cancelLike(loginMember, request)
    }
}
