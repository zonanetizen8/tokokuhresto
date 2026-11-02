package com.example

import com.example.data.security.SecurityHelper
import com.example.data.util.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityAndCurrencyTest {

    @Test
    fun testSecurityHelper_hashAndVerification() {
        val salt = SecurityHelper.generateSalt()
        assertEquals(32, salt.length)

        val password = "secretPassword123"
        val hash = SecurityHelper.hashWithSalt(password, salt)

        assertTrue(SecurityHelper.verifyHash(password, salt, hash))
        assertFalse(SecurityHelper.verifyHash("wrongPassword", salt, hash))

        // Different salt produces different hash
        val salt2 = SecurityHelper.generateSalt()
        val hash2 = SecurityHelper.hashWithSalt(password, salt2)
        assertNotEquals(hash, hash2)
    }

    @Test
    fun testSecurityHelper_pinHashing() {
        val pin = "123456"
        val pinHash = SecurityHelper.hashPin(pin)
        val pinHashAgain = SecurityHelper.hashPin(pin)
        assertEquals(pinHash, pinHashAgain)

        val wrongPinHash = SecurityHelper.hashPin("654321")
        assertNotEquals(pinHash, wrongPinHash)
    }

    @Test
    fun testCurrencyFormatter_rupiah() {
        val formattedZero = CurrencyFormatter.formatRupiah(0L)
        assertTrue(formattedZero.contains("0"))

        val formatted25k = CurrencyFormatter.formatRupiah(25000L)
        assertTrue(formatted25k.contains("25.000") || formatted25k.contains("25,000"))

        val formattedMillion = CurrencyFormatter.formatRupiah(1500000L)
        assertTrue(formattedMillion.contains("1.500.000") || formattedMillion.contains("1,500,000"))
    }
}
