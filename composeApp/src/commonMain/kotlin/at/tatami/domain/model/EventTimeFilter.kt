package at.tatami.domain.model

/**
 * Enum representing the time filter options for viewing events.
 */
enum class EventTimeFilter {
    /**
     * Show events that haven't started yet or are currently ongoing
     */
    UPCOMING,

    /**
     * Show events that have already ended
     */
    PAST
}