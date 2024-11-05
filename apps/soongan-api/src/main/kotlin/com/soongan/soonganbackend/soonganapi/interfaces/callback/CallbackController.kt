package com.soongan.soonganbackend.soonganapi.interfaces.callback

import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.CALLBACK)
@Hidden
class CallbackController {

    @PostMapping(Uri.APPLE_LOGIN)
    fun appleLogin(@RequestParam id_token: String): String {
        return "redirect:/api/${Uri.CALLBACK}${Uri.APPLE_LOGIN}/success?id_token=${id_token}"
    }

    @GetMapping("${Uri.APPLE_LOGIN}${Uri.SUCCESS}")
    fun appleLoginSuccess(@RequestParam id_token: String): String {
        return id_token
    }
}