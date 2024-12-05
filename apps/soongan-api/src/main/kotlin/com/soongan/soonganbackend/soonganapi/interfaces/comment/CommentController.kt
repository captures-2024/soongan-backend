package com.soongan.soonganbackend.soonganapi.interfaces.comment

import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request.CommentSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request.CommentUpdateRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.response.GetCommentResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.response.GetMyCommentResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.response.GetCommentReplyResponseDto
import com.soongan.soonganbackend.soonganapi.service.comment.CommentService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping(Uri.COMMENTS)
@RestController
@Tag(name = "Comments Apis", description = "댓글 관련 API")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping
    @Operation(summary = "콘테스트 게시글 댓글 작성 api", description = "콘테스트 게시글의 댓글을 작성합니다.")
    fun saveComment(
        @LoginMember loginMember: MemberEntity,
        @RequestBody request: CommentSaveRequestDto
    ) {
        commentService.saveComment(loginMember, request)
    }

    @GetMapping
    @Operation(summary = "콘테스트 게시글 댓글 조회 api", description = "한 콘테스트 게시글의 댓글을 조회합니다.")
    fun getPostComments(
        @RequestParam contestType: ContestTypeEnum,
        @RequestParam postId: Long,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): GetCommentResponseDto {
        return commentService.getPostComments(postId, contestType, page, size)
    }

    @GetMapping(Uri.REPLIES)
    @Operation(summary = "콘테스트 게시글 대댓글 조회 api", description = "한 콘테스트 게시글의 대댓글을 조회합니다.")
    fun getCommentsReplies(
        @RequestParam contestType: ContestTypeEnum,
        @RequestParam parentCommentId: Long,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): GetCommentReplyResponseDto {
        return commentService.getCommentsReplies(contestType, parentCommentId, page, size)
    }

    @GetMapping(Uri.MY_HISTORY)
    @Operation(summary = "내가 작성한 콘테스트 게시글 댓글 조회 api", description = "내가 작성한 콘테스트 게시글의 댓글들을 조회합니다.")
    fun getMyComments(
        @LoginMember loginMember: MemberEntity,
        @RequestParam contestType: ContestTypeEnum,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): GetMyCommentResponseDto {
        return commentService.getMyPostComments(loginMember, contestType, page, size)
    }

    @PutMapping
    @Operation(summary = "콘테스트 게시글 댓글 수정 api", description = "콘테스트 게시글의 댓글을 수정합니다.")
    fun updateComment(@LoginMember loginMember: MemberEntity, @RequestBody request: CommentUpdateRequestDto) {
        commentService.updateComment(loginMember, request)
    }

    @DeleteMapping
    @Operation(summary = "콘테스트 게시글 댓글 삭제 api", description = "콘테스트 게시글의 댓글을 삭제합니다.")
    fun deleteComment(@LoginMember loginMember: MemberEntity, @RequestBody commentId: Long) {
        commentService.deleteComment(loginMember, commentId)
    }
}
