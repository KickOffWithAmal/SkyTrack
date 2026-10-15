package com.skypass.skytrack.util

/**
 * Utility for robust semantic version comparison.
 * Supports standard semver (1.0.0, 1.0.1, 1.1.0, 2.0.0),
 * prefixes like 'v' or 'V' (v1.0.1), suffixes like "-beta" or "+build",
 * and varying length versions (1.0 vs 1.0.1).
 */
object VersionComparator {

    /**
     * Returns true if [remoteVersion] is strictly newer (higher) than [currentVersion].
     *
     * Examples:
     * - isNewer("1.0.1", "1.0.0") -> true
     * - isNewer("1.1.0", "1.0.1") -> true
     * - isNewer("v1.0.1", "1.0") -> true (1.0.1 > 1.0.0)
     * - isNewer("1.0.0", "1.0") -> false (1.0.0 == 1.0.0)
     * - isNewer("1.0.0", "1.0.0") -> false
     * - isNewer("0.9.9", "1.0.0") -> false
     * - isNewer("2.0", "1.9.9") -> true
     */
    fun isNewer(remoteVersion: String?, currentVersion: String?): Boolean {
        if (remoteVersion.isNullOrBlank() || currentVersion.isNullOrBlank()) return false

        val remoteParts = parseVersionParts(remoteVersion)
        val currentParts = parseVersionParts(currentVersion)

        if (remoteParts.isEmpty() || currentParts.isEmpty()) return false

        val maxLen = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }

        return false
    }

    /**
     * Extracts integer segments from a version string.
     * Strips leading 'v'/'V', removes build/prerelease tags like "-beta" or "+build".
     */
    fun parseVersionParts(version: String): List<Int> {
        val clean = version.trim()
            .removePrefix("v")
            .removePrefix("V")
            .split("-")[0] // strip prerelease suffix e.g. -beta, -rc1
            .split("+")[0] // strip build metadata e.g. +100
            .trim()

        return clean.split(".")
            .mapNotNull { it.trim().toIntOrNull() }
    }

    /**
     * Formats a version string for display (e.g. "v1.0.1" -> "1.0.1").
     */
    fun cleanVersionName(version: String): String {
        return version.trim()
            .removePrefix("v")
            .removePrefix("V")
            .split("+")[0]
            .trim()
    }
}
