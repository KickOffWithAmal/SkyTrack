package com.skypass.skytrack.util

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    /**
     * Checks if a JWT token is expired.
     *
     * @param token The JWT access token (format: header.payload.signature).
     * @param bufferSeconds Safety leeway in seconds before the actual 'exp' timestamp (default: 30s).
     * @return true if the token is null/blank, malformed, or has expired. false if valid.
     */
    fun isTokenExpired(token: String?, bufferSeconds: Long = 30L): Boolean {
        if (token.isNullOrBlank()) return true
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return true

            var payload = parts[1]
            val remainder = payload.length % 4
            if (remainder > 0) {
                payload += "=".repeat(4 - remainder)
            }

            val payloadBytes = Base64.decode(payload, Base64.URL_SAFE)
            val jsonString = String(payloadBytes, Charsets.UTF_8)
            val json = JSONObject(jsonString)

            if (!json.has("exp")) {
                // If there is no exp claim in token, assume not expired (server will validate)
                return false
            }

            val expSeconds = json.getLong("exp")
            val currentSeconds = System.currentTimeMillis() / 1000L
            currentSeconds >= (expSeconds - bufferSeconds)
        } catch (e: Exception) {
            // If the token cannot be parsed, treat as expired so user is prompted to sign in
            true
        }
    }
}
