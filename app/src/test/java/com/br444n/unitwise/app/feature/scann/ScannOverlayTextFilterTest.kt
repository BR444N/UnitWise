package com.br444n.unitwise.app.feature.scann

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ScannOverlayTextFilterTest {

    private val config = OverlayFrameConfig(
        mediaWidth = 1000,
        mediaHeight = 1000,
        rotation = 0,
        previewWidth = 1000,
        previewHeight = 1000,
        overlayHeight = 1000
    )

    @Test
    fun `returns text when block center is inside overlay`() {
        val result = ScannOverlayTextFilter.filterToOverlay(
            blocks = listOf(
                OverlayTextBlock(
                    text = "500g",
                    centerX = 500f,
                    centerY = 500f
                )
            ),
            config = config
        )

        assertThat(result).containsExactly("500g")
    }

    @Test
    fun `excludes text when block center is outside overlay`() {
        val result = ScannOverlayTextFilter.filterToOverlay(
            blocks = listOf(
                OverlayTextBlock(
                    text = "outside",
                    centerX = 50f,
                    centerY = 50f
                )
            ),
            config = config
        )

        assertThat(result).isEmpty()
    }

    @Test
    fun `supports rotated image dimensions`() {
        val rotatedConfig = config.copy(
            mediaWidth = 720,
            mediaHeight = 1280,
            rotation = 90,
            previewWidth = 1080,
            previewHeight = 1920,
            overlayHeight = 1400
        )

        val result = ScannOverlayTextFilter.filterToOverlay(
            blocks = listOf(
                OverlayTextBlock(
                    text = "price",
                    centerX = 640f,
                    centerY = 260f
                )
            ),
            config = rotatedConfig
        )

        assertThat(result).containsExactly("price")
    }

    @Test
    fun `ignores blank texts even when inside overlay`() {
        val result = ScannOverlayTextFilter.filterToOverlay(
            blocks = listOf(
                OverlayTextBlock(
                    text = "   ",
                    centerX = 500f,
                    centerY = 500f
                )
            ),
            config = config
        )

        assertThat(result).isEmpty()
    }
}
