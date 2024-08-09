package com.soongan.soonganbackend.interfaces.postLike

import com.soongan.soonganbackend.aspects.CheckMember
import com.soongan.soonganbackend.interfaces.postLike.dto.PostLikeRequestDto
import com.soongan.soonganbackend.interfaces.postLike.dto.PostLikeResponseDto
import com.soongan.soonganbackend.persistence.member.MemberEntity
import com.soongan.soonganbackend.service.postLike.PostLikeService
import com.soongan.soonganbackend.util.common.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
    @CheckMember
    fun addLikePost(loginMember: MemberEntity, @RequestBody postLikeRequest: PostLikeRequestDto): PostLikeResponseDto {
        return postLikeService.addLikePost(loginMember, postLikeRequest)
    }
}
