package com.soongan.soonganbackend.soongansupport.util

import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object ImageUtil {

    /**
     * 이미지 파일에서 가로세로 비율(width / height)을 계산합니다.
     *
     * @param imageFile 이미지 파일
     * @return 가로세로 비율 (width / height). 이미지를 읽을 수 없는 경우 null 반환
     */
    fun calculateImageRatio(imageFile: MultipartFile): Double? {
        return try {
            val bufferedImage: BufferedImage = ImageIO.read(imageFile.inputStream) ?: return null
            val width = bufferedImage.width
            val height = bufferedImage.height

            if (height == 0) {
                null
            } else {
                width.toDouble() / height.toDouble()
            }
        } catch (e: Exception) {
            null
        }
    }
}