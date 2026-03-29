package com.br444n.unitwise.app.feature.scann

data class OverlayTextBlock(
    val text: String,
    val centerX: Float,
    val centerY: Float
)

data class OverlayFrameConfig(
    val mediaWidth: Int,
    val mediaHeight: Int,
    val rotation: Int,
    val previewWidth: Int,
    val previewHeight: Int,
    val overlayHeight: Int
)

object ScannOverlayTextFilter {
    fun filterToOverlay(
        blocks: List<OverlayTextBlock>,
        config: OverlayFrameConfig
    ): List<String> {
        if (
            config.mediaWidth <= 0 ||
            config.mediaHeight <= 0 ||
            config.previewWidth <= 0 ||
            config.previewHeight <= 0 ||
            config.overlayHeight <= 0
        ) {
            return emptyList()
        }

        val imageWidth = if (config.rotation == 90 || config.rotation == 270) {
            config.mediaHeight
        } else {
            config.mediaWidth
        }
        val imageHeight = if (config.rotation == 90 || config.rotation == 270) {
            config.mediaWidth
        } else {
            config.mediaHeight
        }

        val scale = maxOf(
            config.previewWidth.toFloat() / imageWidth,
            config.previewHeight.toFloat() / imageHeight
        )
        val offsetX = (imageWidth * scale - config.previewWidth) / 2f
        val offsetY = (imageHeight * scale - config.previewHeight) / 2f

        val targetWidth = config.previewWidth * 0.85f
        val targetHeight = targetWidth * 0.40f
        val targetLeft = (config.previewWidth - targetWidth) / 2f
        val targetTop = (config.overlayHeight - targetHeight) / 2f
        val targetRight = targetLeft + targetWidth
        val targetBottom = targetTop + targetHeight

        return blocks
            .filter { block ->
                val mappedCenterX = block.centerX * scale - offsetX
                val mappedCenterY = block.centerY * scale - offsetY
                mappedCenterX in targetLeft..targetRight && mappedCenterY in targetTop..targetBottom
            }
            .map { it.text.trim() }
            .filter { it.isNotBlank() }
    }
}
