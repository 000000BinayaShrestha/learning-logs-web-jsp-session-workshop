package com.learninglogs.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtil — BCrypt password hashing utility.
 * New for Week 5: bridges the lecture theory on hashing to actual code.
 *
 * Uses the jBCrypt library to hash and verify passwords.
 */
public class PasswordUtil {

    // Cost factor: 2^COST iterations. 10-12 is typical.
    // Higher = slower = more brute-force resistant, but slower login.
    private static final int COST = 10;

    public static String getHashPassword(String inputPassword) {
        String salt = BCrypt.gensalt(COST);
        return BCrypt.hashpw(inputPassword, salt);
    }

    public static boolean checkPassword(String passwordTyped, String hashedPassword) {
        return BCrypt.checkpw(passwordTyped, hashedPassword);
    }
}
