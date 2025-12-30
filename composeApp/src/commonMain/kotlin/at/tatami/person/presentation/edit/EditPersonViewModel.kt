package at.tatami.person.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.common.domain.validation.validators.PersonValidator
import at.tatami.core.ImageType
import at.tatami.domain.model.Sex
import at.tatami.person.domain.usecase.GetPersonByIdUseCase
import at.tatami.person.domain.usecase.UpdatePersonUseCase
import at.tatami.person.domain.usecase.UploadPersonProfileImageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditPersonViewModel(
    private val personId: String,
    private val getPersonByIdUseCase: GetPersonByIdUseCase,
    private val updatePersonUseCase: UpdatePersonUseCase,
    private val uploadPersonProfileImageUseCase: UploadPersonProfileImageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditPersonState())
    val state: StateFlow<EditPersonState> = _state.asStateFlow()

    init {
        loadPerson()
    }

    private fun loadPerson() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val person = getPersonByIdUseCase(personId)
                if (person != null) {
                    _state.update { it.copy(
                        person = person,
                        firstName = person.firstName,
                        lastName = person.lastName,
                        yearOfBirth = person.yearOfBirth,
                        yearOfBirthText = person.yearOfBirth.toString(),
                        sex = person.sex,
                        isLoading = false
                    ) }
                } else {
                    _state.update { it.copy(
                        isLoading = false,
                        errorMessage = "Person not found"
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    errorMessage = "Failed to load person: ${e.message}"
                ) }
            }
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

    fun onProfileImageSelected(imageData: ByteArray?) {
        _state.update { it.copy(
            selectedProfileImage = imageData,
            hasImageChanged = true
        ) }
    }

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

    fun savePerson() {
        viewModelScope.launch {
            val currentState = _state.value
            val originalPerson = currentState.person ?: return@launch

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

            _state.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                // Update person data
                val updatedPerson = originalPerson.copy(
                    firstName = currentState.firstName.trim(),
                    lastName = currentState.lastName.trim(),
                    yearOfBirth = currentState.yearOfBirth!!,
                    sex = currentState.sex!!
                )

                updatePersonUseCase(updatedPerson).fold(
                    onSuccess = { savedPerson ->
                        // Upload image if changed
                        if (currentState.hasImageChanged && currentState.selectedProfileImage != null) {
                            uploadPersonProfileImageUseCase(
                                savedPerson,
                                currentState.selectedProfileImage,
                                ImageType.JPEG
                            ).fold(
                                onSuccess = {
                                    _state.update { it.copy(
                                        isSaving = false,
                                        saveSuccess = true
                                    ) }
                                },
                                onFailure = { error ->
                                    // Person saved but image upload failed
                                    _state.update { it.copy(
                                        isSaving = false,
                                        errorMessage = "Person saved but image upload failed: ${error.message}"
                                    ) }
                                }
                            )
                        } else {
                            _state.update { it.copy(
                                isSaving = false,
                                saveSuccess = true
                            ) }
                        }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(
                            isSaving = false,
                            errorMessage = "Failed to save person: ${error.message}"
                        ) }
                    }
                )
            } catch (e: Exception) {
                _state.update { it.copy(
                    isSaving = false,
                    errorMessage = "Failed to save person: ${e.message}"
                ) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
