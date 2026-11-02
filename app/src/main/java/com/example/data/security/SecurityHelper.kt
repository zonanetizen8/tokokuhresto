package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityHelper {

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    fun hashWithSalt(input: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val combined = input + salt
        val hash = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun verifyHash(input: String, salt: String, expectedHash: String): Boolean {
        val computed = hashWithSalt(input, salt)
        return computed.equals(expectedHash, ignoreCase = true)
    }

    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(("tokokuh_resto_pin_" + pin).toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
