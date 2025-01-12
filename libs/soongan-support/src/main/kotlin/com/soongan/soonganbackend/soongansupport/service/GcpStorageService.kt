package com.soongan.soonganbackend.soongansupport.service

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
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

    private val bucket = env.getProperty("spring.cloud.gcp.storage.bucket")

    fun uploadProfileImage(file: MultipartFile, memberId: Long): String {
        val fileName = file.originalFilename?.substringBeforeLast(".")
        val fileType = file.originalFilename?.substringAfterLast(".")
        val blobId = BlobId.of(bucket, "${memberId}/profile-image/${fileName}-${System.currentTimeMillis()}.${fileType}")
        return uploadImage(blobId, file)
    }

    fun uploadContestImage(file: MultipartFile, memberId: Long, contestType: ContestTypeEnum, round: Int): String {
        val fileName = file.originalFilename?.substringBeforeLast(".")
        val fileType = file.originalFilename?.substringAfterLast(".")
        val blobId = BlobId.of(bucket, "${memberId}/${contestType.type}/${round}/${fileName}-${System.currentTimeMillis()}.${fileType}")
        return uploadImage(blobId, file)
    }

    private fun uploadImage(blobId: BlobId, file: MultipartFile): String {
        val contentType = file.contentType ?: when (file.originalFilename?.substringAfterLast(".")?.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "bmp" -> "image/bmp"
            "webp" -> "image/webp"
            "svg" -> "image/svg+xml"
            else -> "application/octet-stream"
        }

        val blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(contentType)
            .setContentDisposition("inline")
            .build()
        gcpStorage.create(blobInfo, file.inputStream.readBytes())
        return "https://storage.googleapis.com/${blobId.bucket}/${blobId.name}"
    }

    fun deleteFile(fileUrl: String) {
        val blobName = fileUrl.substringAfter("/${bucket}/")
        val decodedBlobName = URLDecoder.decode(blobName, StandardCharsets.UTF_8.name())
        gcpStorage.delete(bucket, decodedBlobName)
    }

    fun deleteMemberFiles(memberId: Long) {
        val bucketName = env.getProperty("spring.cloud.gcp.storage.bucket")
        val prefix = "$memberId/"
        val blobs = gcpStorage.list(bucketName, Storage.BlobListOption.prefix(prefix))
        blobs.iterateAll().forEach { gcpStorage.delete(it.blobId) }
    }
}
