package com.example.security

import org.mindrot.jbcrypt.BCrypt

/**
 * Object responsible for hashing and verifying passwords using the BCrypt algorithm.
 */
object Hashing {

    /**
     * Hashes the given password using BCrypt.
     *
     * @param password The plain text password to be hashed.
     * @return The hashed version of the password.
     */
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    /**
     * Verifies that a plain text password matches a given hashed password.
     *
     * @param password The plain text password.
     * @param hashed The hashed password to compare against.
     * @return True if the password matches the hashed password, false otherwise.
     */
    fun verifyPassword(password: String, hashed: String): Boolean {
        return BCrypt.checkpw(password, hashed)
    }
}
