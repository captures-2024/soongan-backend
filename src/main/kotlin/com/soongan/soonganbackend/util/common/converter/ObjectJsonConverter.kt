package com.soongan.soonganbackend.util.common.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

object ObjectJsonConverter {

    private val objectMapper = ObjectMapper().registerModule(
        KotlinModule.Builder()
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    ).registerModule(JavaTimeModule())

    fun serialize(obj: Any?): String? {
        return objectMapper.writeValueAsString(obj)
    }

    fun deserialize(json: String?, clazz: Class<*>): Any? {
        return objectMapper.readValue(json, clazz)
    }
}
