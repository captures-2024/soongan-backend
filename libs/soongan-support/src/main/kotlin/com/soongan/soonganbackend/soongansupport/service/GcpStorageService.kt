package com.soongan.soonganbackend.soongansupport.service

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import io.github.oshai.kotlinlogging.KotlinLogging
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
    private val logger = KotlinLogging.logger {}
    private val bucket = env.getProperty("spring.cloud.gcp.storage.bucket")

    fun uploadProfileImage(file: MultipartFile, memberId: Long): String {
        val fileName = file.originalFilename?.substringBeforeLast(".")
        val fileType = file.originalFilename?.substringAfterLast(".")
        val blobId = BlobId.of(bucket, "${memberId}/profile-image/${fileName}-${System.currentTimeMillis()}.${fileType}")
        logger.info { "[Storage] upload_attempt type=profile, user=$memberId, fileName=$fileName, fileType=$fileType, size=${file.size}" }
        return uploadImage(blobId, file, "profile", memberId)
    }

    fun uploadContestImage(file: MultipartFile, memberId: Long, contestType: ContestTypeEnum, round: Int): String {
        val fileName = file.originalFilename?.substringBeforeLast(".")
        val fileType = file.originalFilename?.substringAfterLast(".")
        val blobId = BlobId.of(bucket, "${memberId}/${contestType.type}/${round}/${fileName}-${System.currentTimeMillis()}.${fileType}")
        logger.info { "[Storage] upload_attempt type=contest, user=$memberId, contestType=${contestType.type}, round=$round, fileName=$fileName, fileType=$fileType, size=${file.size}" }
        return uploadImage(blobId, file, "contest", memberId)
    }

    private fun uploadImage(blobId: BlobId, file: MultipartFile, type: String, memberId: Long): String {
        val startTime = System.currentTimeMillis()

        try {
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

            val duration = System.currentTimeMillis() - startTime
            val url = "https://storage.googleapis.com/${blobId.bucket}/${blobId.name}"
            logger.info { "[Storage] upload_success type=$type, user=$memberId, path=${blobId.name}, size=${file.size}, duration=${duration}ms" }
            return url
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error(e) { "[Storage] upload_failed type=$type, user=$memberId, path=${blobId.name}, size=${file.size}, duration=${duration}ms, reason=${e.message}" }
            throw e
        }
    }

    fun deleteFile(fileUrl: String) {
        logger.info { "[Storage] delete_attempt url=$fileUrl" }
        try {
            val blobName = fileUrl.substringAfter("/${bucket}/")
            val decodedBlobName = URLDecoder.decode(blobName, StandardCharsets.UTF_8.name())
            val deleted = gcpStorage.delete(bucket, decodedBlobName)

            if (deleted) {
                logger.info { "[Storage] delete_success path=$decodedBlobName" }
            } else {
                logger.warn { "[Storage] delete_notfound path=$decodedBlobName" }
            }
        } catch (e: Exception) {
            logger.error(e) { "[Storage] delete_failed url=$fileUrl, reason=${e.message}" }
            throw e
        }
    }

    fun deleteMemberFiles(memberId: Long) {
        logger.info { "[Storage] bulk_delete_attempt user=$memberId" }
        try {
            val bucketName = env.getProperty("spring.cloud.gcp.storage.bucket")
            val prefix = "$memberId/"
            val blobs = gcpStorage.list(bucketName, Storage.BlobListOption.prefix(prefix))

            var deletedCount = 0
            blobs.iterateAll().forEach { blob ->
                try {
                    gcpStorage.delete(blob.blobId)
                    deletedCount++
                } catch (e: Exception) {
                    logger.error(e) { "[Storage] bulk_delete_item_failed user=$memberId, path=${blob.name}, reason=${e.message}" }
                }
            }

            logger.info { "[Storage] bulk_delete_success user=$memberId, deletedCount=$deletedCount" }
        } catch (e: Exception) {
            logger.error(e) { "[Storage] bulk_delete_failed user=$memberId, reason=${e.message}" }
            throw e
        }
    }
}
