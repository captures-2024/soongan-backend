package com.soongan.soonganbackend.interfaces.callback

import com.soongan.soonganbackend.util.common.constant.Uri
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping(Uri.CALLBACK)
@Hidden
class CallbackController {

    @PostMapping(Uri.APPLE_LOGIN)
    fun androidAppleLogin(@RequestParam("id_token") idToken: String): RedirectView {
        val redirectUrl = "https://www.soongan.site" + Uri.CALLBACK + Uri.APPLE_LOGIN + Uri.SUCCESS
        val redirectView = RedirectView()
        redirectView.url = "${redirectUrl}?id_token=${idToken}"
        return redirectView
    }

    @GetMapping(Uri.APPLE_LOGIN + Uri.SUCCESS)
    fun androidAppleLoginRedirect(@RequestParam("id_token") idToken: String): String {
        return idToken
    }
}