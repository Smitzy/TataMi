package at.tatami.person.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.auth.domain.usecase.GetCurrentUserUseCase
import at.tatami.auth.domain.usecase.SignOutUseCase
import at.tatami.domain.model.Person
import at.tatami.person.domain.usecase.ObservePersonsByUserUseCase
import at.tatami.person.domain.usecase.SetSelectedPersonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonListState(
    val isLoading: Boolean = true,
    val persons: List<Person> = emptyList(),
    val error: String? = null,
    val isEditMode: Boolean = false
)

class PersonListViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observePersonsByUserUseCase: ObservePersonsByUserUseCase,
    private val setSelectedPersonUseCase: SetSelectedPersonUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(PersonListState())
    val state: StateFlow<PersonListState> = _state.asStateFlow()
    
    init {
        loadPersons()
    }
    
    private fun loadPersons() {
        viewModelScope.launch {
            try {
                val currentUser = getCurrentUserUseCase()
                if (currentUser != null) {
                    observePersonsByUserUseCase(currentUser.id).collect { persons ->
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                persons = persons,
                                error = null
                            )
                        }
                    }
                } else {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = "user_not_found"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    fun selectPersonAndNavigate(
        person: Person,
        onNavigate: (Person) -> Unit
    ) {
        viewModelScope.launch {
            // Wait for person selection to complete
            setSelectedPersonUseCase(person.id)
            // Trigger navigation after selection is set
            onNavigate(person)
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }

    fun toggleEditMode() {
        _state.update { it.copy(isEditMode = !it.isEditMode) }
    }
}