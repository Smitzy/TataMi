package at.tatami.common.util

import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Converts an Instant to LocalDateTime using the system's default timezone.
 * Use this for displaying times in the user's local timezone.
 */
@OptIn(ExperimentalTime::class)
fun Instant.toLocalDateTimeInSystemTimeZone(): LocalDateTime =
    toLocalDateTime(TimeZone.currentSystemDefault())