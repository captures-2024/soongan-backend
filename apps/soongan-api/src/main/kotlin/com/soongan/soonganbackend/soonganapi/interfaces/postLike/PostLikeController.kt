package com.soongan.soonganbackend.soonganapi.interfaces.postLike

import com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto.request.PostLikeRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto.response.PostLikeResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganapi.service.postLike.PostLikeService
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.POSTS + Uri.LIKE)
@Tag(name = "Post Like Apis", description = "게시글 좋아요 관련 API")
class PostLikeController (
    private val postLikeService: PostLikeService
){

    @PutMapping
    @Operation(summary = "게시글 좋아요 Api", description = "게시글에 좋아요를 추가합니다.")
    fun addLikePost(@LoginMember loginMember: MemberEntity, @RequestBody postLikeRequest: PostLikeRequestDto): PostLikeResponseDto {
        return postLikeService.addLike(loginMember, postLikeRequest)
    }

    @DeleteMapping
    @Operation(summary = "게시글 좋아요 취소 Api", description = "게시글에 좋아요를 취소합니다.")
    fun cancelLikePost(
        @LoginMember loginMember: MemberEntity,
        @RequestBody postLikeRequest: PostLikeRequestDto
    ): PostLikeResponseDto {
        return postLikeService.cancelLike(loginMember, postLikeRequest)
    }
}
