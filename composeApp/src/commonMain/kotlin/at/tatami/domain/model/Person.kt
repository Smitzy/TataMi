package at.tatami.domain.model

data class Person(
    val id: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val yearOfBirth: Int,
    val sex: Sex,
    val personImgUrl: String? = null,
    val clubIds: List<String> = emptyList()
)

enum class Sex {
    MALE,
    FEMALE,
    OTHER
}