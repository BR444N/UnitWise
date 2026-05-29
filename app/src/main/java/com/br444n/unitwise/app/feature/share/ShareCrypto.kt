package com.br444n.unitwise.app.feature.share

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val AES_MODE = "AES/GCM/NoPadding"
private const val GCM_TAG_LENGTH_BITS = 128
private const val KEY_SIZE_BYTES = 32
private const val IV_SIZE_BYTES = 12

internal object ShareCrypto {

    /**
     * Derives a deterministic 32-byte Key and 12-byte IV from the payload.
     * Uses SHA-512 to generate 64 bytes of entropy, splitting it into:
     * - Key: first 32 bytes
     * - IV: next 12 bytes
     * - Extra bytes: remaining 20 bytes (returned for other purposes, e.g. Share ID)
     */
    fun deriveDeterministicMaterial(payload: String): Triple<String, String, ByteArray> {
        val digest = java.security.MessageDigest.getInstance("SHA-512")
        val hashBytes = digest.digest(payload.toByteArray(Charsets.UTF_8))
        
        val keyBytes = hashBytes.copyOfRange(0, KEY_SIZE_BYTES)
        val ivBytes = hashBytes.copyOfRange(KEY_SIZE_BYTES, KEY_SIZE_BYTES + IV_SIZE_BYTES)
        val extraBytes = hashBytes.copyOfRange(KEY_SIZE_BYTES + IV_SIZE_BYTES, hashBytes.size)
        
        return Triple(keyBytes.toBase64Url(), ivBytes.toBase64Url(), extraBytes)
    }

    fun encrypt(plainText: String, encodedKey: String, encodedIv: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(encodedKey.fromBase64Url(), "AES"),
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, encodedIv.fromBase64Url())
        )
        return cipher.doFinal(plainText.toByteArray(Charsets.UTF_8)).toBase64Url()
    }

    fun decrypt(cipherText: String, encodedKey: String, encodedIv: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(encodedKey.fromBase64Url(), "AES"),
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, encodedIv.fromBase64Url())
        )
        return cipher.doFinal(cipherText.fromBase64Url()).toString(Charsets.UTF_8)
    }
}

private fun ByteArray.toBase64Url(): String {
    return Base64.encodeToString(this, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}

private fun String.fromBase64Url(): ByteArray {
    return Base64.decode(this, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}
