package at.tatami.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing a training session for a group.
 * Trainings are scheduled sessions where group members practice together.
 *
 * @property id Unique identifier for the training
 * @property clubId ID of the club this training belongs to
 * @property groupId ID of the group this training is for
 * @property startDateTime When the training session starts
 * @property notes Optional notes about the training (e.g., focus areas, equipment needed)
 * @property attendedPersonIds List of person IDs who attended this training
 */
data class Training(
    val id: String,
    val clubId: String,
    val groupId: String,
    val startDateTime: LocalDateTime,
    val notes: String = "",
    val attendedPersonIds: List<String> = emptyList()
)