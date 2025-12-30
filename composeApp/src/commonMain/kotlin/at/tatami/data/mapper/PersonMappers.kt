package at.tatami.data.mapper

import at.tatami.data.model.FirebasePerson
import at.tatami.domain.model.Sex
import at.tatami.domain.model.Person

/**
 * Mappers for Person-related domain models
 */

fun FirebasePerson.toDomain(id: String): Person {
    return Person(
        id = id,
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        yearOfBirth = yearOfBirth,
        sex = Sex.valueOf(sex),
        personImgUrl = personImgUrl,
        clubIds = clubIds
    )
}

fun Person.toFirebase() = FirebasePerson(
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    yearOfBirth = yearOfBirth,
    sex = sex.name,
    personImgUrl = personImgUrl,
    clubIds = clubIds
)