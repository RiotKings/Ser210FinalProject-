package com.example.ser210_final_client.util

import java.security.MessageDigest

object PasswordHasher {
    private const val PEPPER = "codegram_ser210"

    fun hash(username: String, plainPassword: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = "$PEPPER|${username.lowercase()}|$plainPassword".toByteArray(Charsets.UTF_8)
        return md.digest(input).joinToString("") { b -> "%02x".format(b) }
    }

    fun verify(username: String, plainPassword: String, storedHash: String): Boolean {
        return hash(username, plainPassword) == storedHash
    }
}
