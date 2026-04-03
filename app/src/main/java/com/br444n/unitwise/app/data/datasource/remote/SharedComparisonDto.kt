package com.br444n.unitwise.app.data.datasource.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SharedComparisonDto(
    @SerialName("share_id")
    val shareId: String,
    @SerialName("ciphertext")
    val ciphertext: String,
    @SerialName("iv")
    val iv: String,
    @SerialName("expires_at")
    val expiresAt: Long,
    @SerialName("created_at")
    val createdAt: Long
)
