package com.soongan.soonganbackend.interfaces.postLike

import com.soongan.soonganbackend.interfaces.postLike.dto.PostLikeRequestDto
import com.soongan.soonganbackend.interfaces.postLike.dto.PostLikeResponseDto
import com.soongan.soonganbackend.service.postLike.PostLikeService
import com.soongan.soonganbackend.util.common.constant.Uri
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.LIKE)
@Tag(name = "Post Like Apis", description = "게시글 좋아요 관련 API")
class PostLikeController (
    private val postLikeService: PostLikeService
){

    @PutMapping
    @Operation(summary = "게시글 좋아요 Api", description = "게시글에 좋아요를 추가합니다.")
    fun addLikePost(
        @RequestBody postLikeRequest: PostLikeRequestDto,
        @AuthenticationPrincipal loginMember: MemberDetail
    ): PostLikeResponseDto {
        return postLikeService.addLikePost(postLikeRequest, loginMember)
    }

    @DeleteMapping
    @Operation(summary = "게시글 좋아요 취소 Api", description = "게시글에 좋아요를 취소합니다.")
    fun cancelLikePost(
        @RequestBody postLikeRequest: PostLikeRequestDto,
        @AuthenticationPrincipal loginMember: MemberDetail
    ): PostLikeResponseDto {
        return postLikeService.cancelLikePost(postLikeRequest, loginMember)
    }
}
