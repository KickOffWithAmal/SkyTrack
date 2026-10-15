package com.skypass.skytrack

import com.skypass.skytrack.util.VersionComparator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionComparatorTest {

    @Test
    fun testSameVersions() {
        assertFalse(VersionComparator.isNewer("1.0.0", "1.0.0"))
        assertFalse(VersionComparator.isNewer("v1.0.0", "1.0.0"))
        assertFalse(VersionComparator.isNewer("1.0", "1.0.0"))
        assertFalse(VersionComparator.isNewer("1.0.0", "1.0"))
    }

    @Test
    fun testPatchIncrement() {
        assertTrue(VersionComparator.isNewer("1.0.1", "1.0.0"))
        assertTrue(VersionComparator.isNewer("v1.0.1", "1.0.0"))
        assertTrue(VersionComparator.isNewer("v1.0.1", "1.0"))
        assertFalse(VersionComparator.isNewer("1.0.0", "1.0.1"))
    }

    @Test
    fun testMinorIncrement() {
        assertTrue(VersionComparator.isNewer("1.1.0", "1.0.0"))
        assertTrue(VersionComparator.isNewer("1.1.0", "1.0.1"))
        assertTrue(VersionComparator.isNewer("v1.1.0", "1.0.9"))
        assertFalse(VersionComparator.isNewer("1.0.9", "1.1.0"))
    }

    @Test
    fun testMajorIncrement() {
        assertTrue(VersionComparator.isNewer("2.0.0", "1.9.9"))
        assertTrue(VersionComparator.isNewer("v2.0", "1.9.9"))
        assertFalse(VersionComparator.isNewer("1.9.9", "2.0.0"))
    }

    @Test
    fun testPrereleaseAndBuildMetadata() {
        // 1.0.1-beta vs 1.0.0 -> base 1.0.1 is newer than 1.0.0
        assertTrue(VersionComparator.isNewer("v1.0.1-beta", "1.0.0"))
        // 1.0.0-rc1 vs 1.0.0 -> base is equal -> false
        assertFalse(VersionComparator.isNewer("v1.0.0-rc1", "1.0.0"))
    }

    @Test
    fun testInvalidOrBlank() {
        assertFalse(VersionComparator.isNewer(null, "1.0.0"))
        assertFalse(VersionComparator.isNewer("1.0.0", null))
        assertFalse(VersionComparator.isNewer("", "1.0.0"))
        assertFalse(VersionComparator.isNewer("invalid", "1.0.0"))
    }

    @Test
    fun testCleanVersionName() {
        assertEquals("1.0.1", VersionComparator.cleanVersionName("v1.0.1"))
        assertEquals("1.0.1", VersionComparator.cleanVersionName("V1.0.1"))
        assertEquals("1.0.1", VersionComparator.cleanVersionName("1.0.1"))
    }
}
