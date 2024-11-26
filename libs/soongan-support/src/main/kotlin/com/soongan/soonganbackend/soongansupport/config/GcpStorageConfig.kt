package com.soongan.soonganbackend.soongansupport.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spring.core.GcpProjectIdProvider
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GcpStorageConfig {

    @Value("\${spring.cloud.gcp.project-id}")
    private lateinit var projectId: String

    @Value("\${spring.cloud.gcp.credentials.key-json-string}")
    private lateinit var credentialsKeyJsonString: String

    @Bean
    fun gcpProjectIdProvider(): GcpProjectIdProvider {
        return GcpProjectIdProvider { projectId }
    }

    @Bean
    fun googleCloudStorage(): Storage {
        return StorageOptions.newBuilder()
            .setProjectId(projectId)
            .setCredentials(GoogleCredentials.fromStream(credentialsKeyJsonString.byteInputStream()))
            .build()
            .service
    }
}
