package com.soongan.soonganbackend.soongansupport.service

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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
        return "https://storage.cloud.google.com/${blob.bucket}/${blob.name}"
    }

    fun deleteFile(fileUrl: String) {
        val bucketName = env.getProperty("spring.cloud.gcp.storage.bucket")
        val blobName = fileUrl.substringAfter("/${bucketName}/")
        val decodedBlobName = URLDecoder.decode(blobName, StandardCharsets.UTF_8.name())
        gcpStorage.delete(bucketName, decodedBlobName)
    }

    fun deleteMemberFiles(memberId: Long) {
        val bucketName = env.getProperty("spring.cloud.gcp.storage.bucket")
        val prefix = "$memberId/"
        val blobs = gcpStorage.list(bucketName, Storage.BlobListOption.prefix(prefix))
        blobs.iterateAll().forEach { gcpStorage.delete(it.blobId) }
    }
}
