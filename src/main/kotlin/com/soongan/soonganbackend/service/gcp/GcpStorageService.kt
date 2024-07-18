package com.soongan.soonganbackend.service.gcp

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class GcpStorageService(
    private val env: Environment,
    private val gcpStorage: Storage
) {

    fun uploadFile(file: MultipartFile, memberId: Long): String {
        val blobId = BlobId.of(env.getProperty("spring.cloud.gcp.storage.bucket"), "${memberId}/${file.originalFilename}")
        val blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(file.contentType)
            .build()
        val blob = gcpStorage.create(blobInfo, file.inputStream.readBytes())
        return "www.googleapis.com/storage/${blob.bucket}/${blob.name}"
    }
}