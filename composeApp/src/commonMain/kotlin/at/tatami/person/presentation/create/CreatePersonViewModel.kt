package at.tatami.person.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.domain.model.Sex
import at.tatami.person.domain.usecase.CreatePersonUseCase
import at.tatami.common.domain.validation.validators.PersonValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.error_create_person_failed
import tatami.composeapp.generated.resources.error_invalid_credentials

class CreatePersonViewModel(
    private val createPersonUseCase: CreatePersonUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreatePersonState())
    val state: StateFlow<CreatePersonState> = _state.asStateFlow()

    private fun validateCurrentState() {
        viewModelScope.launch {
            val currentState = _state.value
            val validationState = PersonValidator.validate(
                firstName = currentState.firstName,
                lastName = currentState.lastName,
                yearOfBirth = currentState.yearOfBirth,
                sex = currentState.sex
            )

            _state.update { it.copy(validation = validationState) }
        }
    }

    fun updateFirstName(firstName: String) {
        _state.update { it.copy(firstName = firstName) }
        validateCurrentState()
    }

    fun updateLastName(lastName: String) {
        _state.update { it.copy(lastName = lastName) }
        validateCurrentState()
    }

    fun updateYearOfBirthText(yearText: String) {
        val filtered = yearText.filter { it.isDigit() }
        if (filtered.length <= 4) {
            val year = filtered.toIntOrNull()
            _state.update { it.copy(
                yearOfBirthText = filtered,
                yearOfBirth = year
            ) }
            validateCurrentState()
        }
    }

    fun updateSex(sex: Sex?) {
        _state.update { it.copy(sex = sex) }
        validateCurrentState()
    }

    fun savePerson() {
        viewModelScope.launch {
            val currentState = _state.value

            // Final validation
            val validationState = PersonValidator.validate(
                firstName = currentState.firstName,
                lastName = currentState.lastName,
                yearOfBirth = currentState.yearOfBirth,
                sex = currentState.sex
            )
            _state.update { it.copy(validation = validationState) }

            if (!validationState.isValid) {
                return@launch
            }

            _state.update { it.copy(isLoading = true) }

            createPersonUseCase(
                firstName = currentState.firstName.trim(),
                lastName = currentState.lastName.trim(),
                yearOfBirth = currentState.yearOfBirth!!,
                sex = currentState.sex!!
            ).fold(
                onSuccess = { createdPerson ->
                    _state.update { it.copy(
                        isLoading = false,
                        createdPersonId = createdPerson.id
                    ) }
                },
                onFailure = { error ->
                    val errorMessage = when {
                        error.message?.contains("PERMISSION_DENIED") == true ->
                            getString(Res.string.error_create_person_failed)
                        error.message?.contains("UNAUTHENTICATED") == true ->
                            getString(Res.string.error_invalid_credentials)
                        else -> error.message ?: getString(Res.string.error_create_person_failed)
                    }

                    _state.update { it.copy(
                        isLoading = false,
                        validation = it.validation.copy(generalError = errorMessage)
                    ) }
                }
            )
        }
    }

    fun clearCreatedPersonId() {
        _state.update { it.copy(createdPersonId = null) }
    }
}