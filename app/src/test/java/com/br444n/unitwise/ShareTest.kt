package com.br444n.unitwise

import org.junit.Assert.assertEquals
import org.junit.Test
import java.security.MessageDigest

class ShareTest {

    private val shareAlphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    private val shareIdLength = 6

    private fun generateDeterministicShareId(extraBytes: ByteArray): String {
        return buildString(shareIdLength) {
            for (i in 0 until shareIdLength) {
                val byteVal = extraBytes[i].toInt() and 0xFF
                append(shareAlphabet[byteVal % shareAlphabet.length])
            }
        }
    }

    @Test
    fun testDeterministicShareId() {
        val payload = "{\"productA\":\"foo\"}"
        val digest = MessageDigest.getInstance("SHA-512")
        val hashBytes = digest.digest(payload.toByteArray(Charsets.UTF_8))
        
        val extraBytes = hashBytes.copyOfRange(44, hashBytes.size)
        
        val shareId1 = generateDeterministicShareId(extraBytes)
        val shareId2 = generateDeterministicShareId(extraBytes)
        
        println("Share ID: $shareId1")
        
        assertEquals("Share IDs deben ser deterministas y coincidir", shareId1, shareId2)
        assertEquals("El tamaño del Share ID debe ser exactamente 6", shareIdLength, shareId1.length)
    }
}
