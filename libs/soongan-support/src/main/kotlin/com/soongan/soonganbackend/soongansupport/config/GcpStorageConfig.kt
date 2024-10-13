package com.soongan.soonganbackend.soongansupport.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.io.File

@Configuration

class GcpStorageConfig(
    private val env: Environment
) {

    @Bean
    fun googleCloudStorage(): Storage {
        return StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(
                env.getProperty("spring.cloud.gcp.credentials.location")?.let {
                    File(it.replace("classpath:", "libs/soongan-support/src/main/resources/")).inputStream()                }
            ))
            .build()
            .service
    }
}
