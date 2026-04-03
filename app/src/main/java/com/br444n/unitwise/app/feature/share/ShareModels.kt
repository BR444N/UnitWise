package com.br444n.unitwise.app.feature.share

import com.br444n.unitwise.app.feature.home.components.ProductInputState

data class SharedComparisonData(
    val shareId: String,
    val productA: ProductInputState,
    val productB: ProductInputState
)

data class SharedComparisonLink(
    val shareId: String,
    val encryptionKey: String,
    val url: String,
    val shareText: String
)

data class SharedComparisonRecord(
    val shareId: String,
    val ciphertext: String,
    val iv: String,
    val expiresAt: Long,
    val createdAt: Long
)
