package at.tatami.main.presentation.scaffold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.domain.model.Club
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MainScaffoldState(
    val selectedClubName: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class MainScaffoldViewModel(
    private val observeSelectedClub: ObserveSelectedClubUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(MainScaffoldState())
    val state: StateFlow<MainScaffoldState> = _state.asStateFlow()
    
    init {
        observeClubSelection()
    }
    
    private fun observeClubSelection() {
        viewModelScope.launch {
            observeSelectedClub()
                .collect { club ->
                    _state.update { 
                        it.copy(
                            selectedClubName = club?.name,
                            isLoading = false,
                            errorMessage = if (club == null) "No club selected" else null
                        ) 
                    }
                }
        }
    }
}