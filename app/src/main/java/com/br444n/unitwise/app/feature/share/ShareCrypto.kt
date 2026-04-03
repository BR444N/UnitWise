package com.br444n.unitwise.app.feature.share

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val AES_MODE = "AES/GCM/NoPadding"
private const val GCM_TAG_LENGTH_BITS = 128
private const val KEY_SIZE_BYTES = 32
private const val IV_SIZE_BYTES = 12

internal object ShareCrypto {
    private val secureRandom = SecureRandom()

    fun generateEncodedKey(): String {
        return randomBytes(KEY_SIZE_BYTES).toBase64Url()
    }

    fun generateEncodedIv(): String {
        return randomBytes(IV_SIZE_BYTES).toBase64Url()
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

    private fun randomBytes(size: Int): ByteArray {
        return ByteArray(size).also(secureRandom::nextBytes)
    }
}

private fun ByteArray.toBase64Url(): String {
    return Base64.encodeToString(this, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}

private fun String.fromBase64Url(): ByteArray {
    return Base64.decode(this, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}
