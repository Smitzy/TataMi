package at.tatami.club.presentation.photoupload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.core.ImageType
import at.tatami.club.domain.usecase.UploadClubProfileImageUseCase
import at.tatami.domain.repository.ClubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClubPhotoUploadViewModel(
    private val clubId: String,
    private val clubRepository: ClubRepository,
    private val uploadClubProfileImageUseCase: UploadClubProfileImageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClubPhotoUploadState())
    val state: StateFlow<ClubPhotoUploadState> = _state.asStateFlow()

    init {
        loadClub()
    }

    private fun loadClub() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val club = clubRepository.getClubById(clubId)
                _state.update { it.copy(
                    club = club,
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    errorMessage = "Failed to load club: ${e.message}"
                ) }
            }
        }
    }

    fun onProfileImageSelected(imageData: ByteArray?) {
        _state.update { it.copy(selectedProfileImage = imageData) }
    }

    fun saveProfileImage() {
        viewModelScope.launch {
            val currentState = _state.value
            val club = currentState.club
            val imageData = currentState.selectedProfileImage

            if (club == null || imageData == null) {
                _state.update { it.copy(
                    errorMessage = "No image selected or club not loaded"
                ) }
                return@launch
            }

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            uploadClubProfileImageUseCase(club, imageData, ImageType.JPEG).fold(
                onSuccess = { updatedClub ->
                    _state.update { it.copy(
                        isLoading = false,
                        club = updatedClub,
                        uploadSuccess = true
                    ) }
                },
                onFailure = { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        errorMessage = "Failed to upload profile image: ${error.message}"
                    ) }
                }
            )
        }
    }

    fun skipPhotoUpload() {
        _state.update { it.copy(uploadSuccess = true) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}