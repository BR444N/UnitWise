package com.br444n.unitwise.app.feature.share

import android.net.Uri
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.SecureRandom
import androidx.core.net.toUri

private object ShareDeepLinkCodec {
    private const val SCHEME = "https"
    private const val HOST = "unitwise-app.vercel.app"
    private const val PATH_PREFIX = "c"
    private const val KEY_FRAGMENT_PARAM = "k"
    private const val SHARE_TTL_MILLIS = 30L * 24L * 60L * 60L * 1000L
    private const val SHARE_ID_LENGTH = 6
    private const val SHARE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    private val random = SecureRandom()

    fun createEncryptedShare(comparison: ComparisonEntity): Pair<SharedComparisonLink, SharedComparisonRecord> {
        val shareId = generateShareId()
        val encryptionKey = ShareCrypto.generateEncodedKey()
        val iv = ShareCrypto.generateEncodedIv()
        val now = System.currentTimeMillis()
        val expiresAt = now + SHARE_TTL_MILLIS
        val payload = SharedComparisonPayload(
            productAName = comparison.productAName,
            productAContent = comparison.productAContent,
            productAUnit = comparison.productAUnit,
            productAPrice = comparison.productAPrice,
            productAQuantity = comparison.productAQuantity,
            productBName = comparison.productBName,
            productBContent = comparison.productBContent,
            productBUnit = comparison.productBUnit,
            productBPrice = comparison.productBPrice,
            productBQuantity = comparison.productBQuantity
        )
        val cipherText = ShareCrypto.encrypt(
            plainText = json.encodeToString(payload),
            encodedKey = encryptionKey,
            encodedIv = iv
        )
        val url = Uri.Builder()
            .scheme(SCHEME)
            .authority(HOST)
            .appendPath(PATH_PREFIX)
            .appendPath(shareId)
            .encodedFragment("$KEY_FRAGMENT_PARAM=$encryptionKey")
            .build()
            .toString()

        return SharedComparisonLink(
            shareId = shareId,
            encryptionKey = encryptionKey,
            url = url,
            shareText = "Compare this result in UnitWise: $url"
        ) to SharedComparisonRecord(
            shareId = shareId,
            ciphertext = cipherText,
            iv = iv,
            expiresAt = expiresAt,
            createdAt = now
        )
    }

    fun decodeComparison(record: SharedComparisonRecord, encryptionKey: String): ComparisonEntity? {
        if (record.expiresAt <= System.currentTimeMillis()) {
            return null
        }

        return runCatching {
            val payload = json.decodeFromString<SharedComparisonPayload>(
                ShareCrypto.decrypt(
                    cipherText = record.ciphertext,
                    encodedKey = encryptionKey,
                    encodedIv = record.iv
                )
            )
            ComparisonEntity(
                shareId = record.shareId,
                productAName = payload.productAName,
                productAContent = payload.productAContent,
                productAUnit = payload.productAUnit,
                productAPrice = payload.productAPrice,
                productAQuantity = payload.productAQuantity,
                productBName = payload.productBName,
                productBContent = payload.productBContent,
                productBUnit = payload.productBUnit,
                productBPrice = payload.productBPrice,
                productBQuantity = payload.productBQuantity
            )
        }.getOrNull()
    }

    fun extractEncryptionKey(uri: Uri?): String? {
        val fragment = uri?.fragment ?: return null
        return "https://unitwise-app.vercel.app/?$fragment".toUri().getQueryParameter(KEY_FRAGMENT_PARAM)
    }

    private fun generateShareId(): String {
        return buildString(SHARE_ID_LENGTH) {
            repeat(SHARE_ID_LENGTH) {
                append(SHARE_ALPHABET[random.nextInt(SHARE_ALPHABET.length)])
            }
        }
    }
}

fun createEncryptedSharedComparison(comparison: ComparisonEntity): Pair<SharedComparisonLink, SharedComparisonRecord> {
    return ShareDeepLinkCodec.createEncryptedShare(comparison)
}

fun decodeSharedComparison(record: SharedComparisonRecord, encryptionKey: String): ComparisonEntity? {
    return ShareDeepLinkCodec.decodeComparison(record, encryptionKey)
}

fun extractSharedComparisonKey(uri: Uri?): String? {
    return ShareDeepLinkCodec.extractEncryptionKey(uri)
}

@Serializable
private data class SharedComparisonPayload(
    val productAName: String,
    val productAContent: String,
    val productAUnit: String,
    val productAPrice: String,
    val productAQuantity: String,
    val productBName: String,
    val productBContent: String,
    val productBUnit: String,
    val productBPrice: String,
    val productBQuantity: String
)
