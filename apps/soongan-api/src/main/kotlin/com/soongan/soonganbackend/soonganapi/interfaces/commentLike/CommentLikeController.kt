package com.soongan.soonganbackend.soonganapi.interfaces.commentLike

import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(Uri.COMMENTS + Uri.LIKE)
@RestController
class CommentLikeController {
}
