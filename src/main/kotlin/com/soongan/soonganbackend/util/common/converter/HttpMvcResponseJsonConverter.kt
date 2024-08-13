package com.soongan.soonganbackend.util.common.converter

import jakarta.servlet.http.HttpServletResponse

object HttpMvcResponseJsonConverter {

    fun <T> writeJsonResponse(response: HttpServletResponse, responseData: T?){
        ObjectJsonConverter.serialize(responseData)?.let {
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            response.writer.write(it)
        }
            ?: response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
    }
}
