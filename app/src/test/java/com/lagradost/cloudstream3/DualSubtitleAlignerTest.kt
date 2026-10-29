package com.lagradost.cloudstream3

import com.lagradost.cloudstream3.ui.player.DualSubtitleAligner
import com.lagradost.cloudstream3.ui.player.SubtitleCue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DualSubtitleAlignerTest {

    @Test
    fun `aligns overlapping primary and secondary cues into single row`() {
        val pCues = listOf(
            SubtitleCue(1000L, 3000L, listOf("Hello world"))
        )
        val sCues = listOf(
            SubtitleCue(1200L, 2800L, listOf("Salam dünya"))
        )

        val aligned = DualSubtitleAligner.align(pCues, 0L, sCues, 0L)
        assertEquals(1, aligned.size)
        assertEquals("Hello world", aligned[0].primaryText)
        assertEquals("Salam dünya", aligned[0].secondaryText)
        assertEquals(1000L, aligned[0].startTimeMs)
    }

    @Test
    fun `deduplicates repeated identical cues without producing multiple rows`() {
        val pCues = listOf(
            SubtitleCue(1000L, 2000L, listOf("Repeated line")),
            SubtitleCue(1500L, 2000L, listOf("Repeated line")),
            SubtitleCue(2000L, 2000L, listOf("Repeated line")),
            SubtitleCue(5000L, 3000L, listOf("Next line"))
        )
        val sCues = listOf(
            SubtitleCue(1000L, 3000L, listOf("Təkrar sətir")),
            SubtitleCue(5000L, 3000L, listOf("Növbəti sətir"))
        )

        val aligned = DualSubtitleAligner.align(pCues, 0L, sCues, 0L)
        assertEquals(2, aligned.size)
        assertEquals("Repeated line", aligned[0].primaryText)
        assertEquals("Təkrar sətir", aligned[0].secondaryText)
        assertEquals("Next line", aligned[1].primaryText)
        assertEquals("Növbəti sətir", aligned[1].secondaryText)
    }

    @Test
    fun `handles empty cues on one side cleanly`() {
        val pCues = listOf(
            SubtitleCue(1000L, 3000L, listOf("Only primary"))
        )
        val sCues = emptyList<SubtitleCue>()

        val aligned = DualSubtitleAligner.align(pCues, 0L, sCues, 0L)
        assertEquals(1, aligned.size)
        assertEquals("Only primary", aligned[0].primaryText)
        assertNull(aligned[0].secondaryText)
    }

    @Test
    fun `filters out blank cues`() {
        val pCues = listOf(
            SubtitleCue(1000L, 3000L, listOf("   ")),
            SubtitleCue(2000L, 3000L, listOf("Valid line"))
        )
        val sCues = listOf(
            SubtitleCue(2000L, 3000L, listOf("Düzgün sətir"))
        )

        val aligned = DualSubtitleAligner.align(pCues, 0L, sCues, 0L)
        assertEquals(1, aligned.size)
        assertEquals("Valid line", aligned[0].primaryText)
        assertEquals("Düzgün sətir", aligned[0].secondaryText)
    }
}
