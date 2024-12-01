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
        val blobId = BlobId.of(bucket, "${memberId}/profile-image/${file.originalFilename}-${System.currentTimeMillis()}")
        return uploadFile(blobId, file)
    }

    fun uploadContestImage(file: MultipartFile, memberId: Long, contestType: ContestTypeEnum, round: Int): String {
        val blobId = BlobId.of(bucket, "${memberId}/${contestType.type}/${round}/${file.originalFilename}-${System.currentTimeMillis()}")
        return uploadFile(blobId, file)
    }

    private fun uploadFile(blobId: BlobId, file: MultipartFile): String {
        val blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.contentType).build()
        val blob = gcpStorage.create(blobInfo, file.inputStream.readBytes())
        return "https://storage.cloud.google.com/${blob.bucket}/${blob.name}"
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
