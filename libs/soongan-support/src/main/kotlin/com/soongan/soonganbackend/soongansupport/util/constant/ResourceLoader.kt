package com.soongan.soonganbackend.soongansupport.util.constant

import org.springframework.core.io.ClassPathResource

object ResourceLoader {
    fun loadResource(name: String) = ClassPathResource(name).inputStream
}