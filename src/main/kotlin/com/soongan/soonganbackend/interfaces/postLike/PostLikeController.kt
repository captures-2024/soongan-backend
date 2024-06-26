package com.soongan.soonganbackend.interfaces.postLike

import com.soongan.soonganbackend.interfaces.postLike.dto.PostLikeRequestDto
import com.soongan.soonganbackend.interfaces.postLike.dto.PostLikeResponseDto
import com.soongan.soonganbackend.service.postLike.PostLikeService
import com.soongan.soonganbackend.util.common.constant.Uri
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import io.swagger.annotations.ApiOperation
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.LIKE)
class PostLikeController (
    private val postLikeService: PostLikeService
){

    @ApiOperation("사진 좋아요")
    @PutMapping
    fun addLikePost(
        @RequestBody postLikeRequest: PostLikeRequestDto,
        @AuthenticationPrincipal loginMember: MemberDetail
    ): PostLikeResponseDto {
        return postLikeService.addLikePost(postLikeRequest, loginMember)
    }
}
