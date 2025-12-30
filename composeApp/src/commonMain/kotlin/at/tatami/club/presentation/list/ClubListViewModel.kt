package at.tatami.club.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.ObservePersonClubsUseCase
import at.tatami.club.domain.usecase.PersonClub
import at.tatami.club.domain.usecase.SetSelectedClubUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import at.tatami.domain.model.Person
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ClubListState(
    val clubs: List<PersonClub> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedPerson: Person? = null
)

class ClubListViewModel(
    private val observePersonClubs: ObservePersonClubsUseCase,
    private val observeSelectedPerson: ObserveSelectedPersonUseCase,
    private val setSelectedClubUseCase: SetSelectedClubUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ClubListState())
    val state: StateFlow<ClubListState> = _state.asStateFlow()
    
    init {
        observeData()
    }
    
    private fun observeData() {
        // Observe selected person
        viewModelScope.launch {
            observeSelectedPerson()
                .collect { person ->
                    _state.update { 
                        it.copy(selectedPerson = person)
                    }
                }
        }
        
        // Observe clubs for the selected person
        viewModelScope.launch {
            observePersonClubs()
                .onStart {
                    _state.update { it.copy(isLoading = true, errorMessage = null) }
                }
                .catch { exception ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to load clubs"
                        )
                    }
                }
                .collect { clubs ->
                    _state.update { 
                        it.copy(
                            clubs = clubs,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }
    
    fun selectClubAndNavigate(
        personClub: PersonClub,
        onNavigate: (PersonClub) -> Unit
    ) {
        viewModelScope.launch {
            // Wait for club selection to complete
            setSelectedClubUseCase(personClub.club.id)
            // Trigger navigation after selection is set
            onNavigate(personClub)
        }
    }
}