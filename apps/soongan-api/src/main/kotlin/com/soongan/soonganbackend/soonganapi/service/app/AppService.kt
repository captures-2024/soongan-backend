package com.soongan.soonganbackend.soonganapi.service.app

import com.soongan.soonganbackend.soonganapi.interfaces.app.dto.request.VersionCheckRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.app.dto.response.VersionCheckResponseDto
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class AppService {

    private val logger = KotlinLogging.logger { }

    companion object {
        // 최신 버전 정의 (플랫폼별)
        private const val LATEST_VERSION_ANDROID = "2.0.1"
        private const val LATEST_VERSION_IOS = "1.0.0"

        // 강제 업데이트가 필요한 최소 버전 (이 버전보다 낮으면 강제 업데이트)
        private const val MIN_VERSION_ANDROID = "2.0.0"
        private const val MIN_VERSION_IOS = "1.0.0"
    }

    fun checkVersion(versionCheckRequestDto: VersionCheckRequestDto): VersionCheckResponseDto {
        val currentVersion = versionCheckRequestDto.appVersion
        val userAgent = versionCheckRequestDto.userAgent

        // 버전 형식 검증 (semantic versioning: major.minor.patch)
        if (!isValidVersionFormat(currentVersion)) {
            throw SoonganException(StatusCode.SOONGAN_API_INVALID_APP_VERSION_FORMAT)
        }

        // 플랫폼별 최신 버전 및 최소 버전 가져오기
        val latestVersion = when (userAgent) {
            UserAgentEnum.ANDROID -> LATEST_VERSION_ANDROID
            UserAgentEnum.IOS -> LATEST_VERSION_IOS
        }

        val minVersion = when (userAgent) {
            UserAgentEnum.ANDROID -> MIN_VERSION_ANDROID
            UserAgentEnum.IOS -> MIN_VERSION_IOS
        }

        // 버전 비교
        val isLatest = compareVersions(currentVersion, latestVersion) >= 0
        val forceUpdate = compareVersions(currentVersion, minVersion) < 0

        // 업데이트 메시지 생성
        val updateMessage = when {
            forceUpdate -> "최신 버전으로 업데이트가 필요합니다. 앱을 계속 사용하려면 업데이트를 진행해주세요."
            !isLatest -> "새로운 버전이 출시되었습니다. 업데이트하시겠어요?"
            else -> null
        }

        logger.info { "Version check - userAgent: $userAgent, currentVersion: $currentVersion, latestVersion: $latestVersion, isLatest: $isLatest, forceUpdate: $forceUpdate" }

        return VersionCheckResponseDto(
            isLatest = isLatest,
            currentVersion = currentVersion,
            latestVersion = latestVersion,
            forceUpdate = forceUpdate,
            updateMessage = updateMessage
        )
    }

    /**
     * Semantic versioning 형식 검증 (major.minor.patch)
     * 예: 1.0.0, 2.1.3
     */
    private fun isValidVersionFormat(version: String): Boolean {
        val versionPattern = Regex("^\\d+\\.\\d+\\.\\d+$")
        return versionPattern.matches(version)
    }

    /**
     * 두 버전을 비교
     * @return 0: 같음, 양수: version1 > version2, 음수: version1 < version2
     */
    private fun compareVersions(version1: String, version2: String): Int {
        val parts1 = version1.split(".").map { it.toInt() }
        val parts2 = version2.split(".").map { it.toInt() }

        for (i in 0 until maxOf(parts1.size, parts2.size)) {
            val v1 = parts1.getOrNull(i) ?: 0
            val v2 = parts2.getOrNull(i) ?: 0

            if (v1 != v2) {
                return v1 - v2
            }
        }

        return 0
    }
}