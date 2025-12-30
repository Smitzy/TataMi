package at.tatami.data.mapper

import at.tatami.data.model.FirebaseGroup
import at.tatami.domain.model.Group

/**
 * Mappers for converting between Group domain models and Firebase models.
 * These are simple 1:1 mappings with no complex transformations.
 */

/**
 * Converts a FirebaseGroup to a domain Group model.
 *
 * @param id The document ID from Firestore
 * @return Domain Group model
 */
fun FirebaseGroup.toDomain(id: String) = Group(
    id = id,
    clubId = clubId,
    name = name,
    memberIds = memberIds,
    trainerIds = trainerIds
)

/**
 * Converts a domain Group model to a FirebaseGroup for storage.
 * Note: The ID is not included as Firestore manages document IDs separately.
 *
 * @return FirebaseGroup for Firestore storage
 */
fun Group.toFirebase() = FirebaseGroup(
    clubId = clubId,
    name = name,
    memberIds = memberIds,
    trainerIds = trainerIds
)