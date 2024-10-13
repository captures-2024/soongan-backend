package com.soongan.soonganbackend.soongansupport.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.soongan.soonganbackend.soongansupport.util.constant.ResourceLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration

class GcpStorageConfig {

    @Bean
    fun googleCloudStorage(): Storage {
        val credentialsStream = ResourceLoader.loadResource("soongan-dev-IAM.json")

        return StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(credentialsStream))
            .build()
            .service
    }
}
