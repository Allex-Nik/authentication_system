package com.example.security

import kotlin.test.*

/**
 * Verifies the functionality of the "Hashing" object,
 * which provides password hashing and verification.
 */
class HashingTest {

    /**
     * Verifies the password hashing and verification.
     * Hashes a password using and then checks:
     * - The hashed password is not null.
     * - The original password is correctly verified against the hash.
     * - A wrong password does not match the hash.
     */
    @Test
    fun `test password hashing and verification`() {
        // Define the password to be hashed
        val password = "password123"

        // Hash the password using Hashing object
        val hash = Hashing.hashPassword(password)

        // Ensure the hashed password is not null
        assertNotNull(hash)

        // Verify that the correct password matches the hash
        assertTrue(Hashing.verifyPassword(password, hash))

        // Verify that an incorrect password does not match the hash
        assertFalse(Hashing.verifyPassword("wrongpassword", hash))
    }
}
