package com.amethamor.sleep.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class DateTimeUtilsTest {
    @Test
    fun parseRecordDateOrNull_returnsDateForValidValue() {
        assertEquals(
            LocalDate.of(2026, 7, 23),
            DateTimeUtils.parseRecordDateOrNull("2026-07-23")
        )
    }

    @Test
    fun parseRecordDateOrNull_returnsNullForInvalidValue() {
        assertNull(DateTimeUtils.parseRecordDateOrNull("2026-99-99"))
    }
}
