package at.tatami.common.domain.service

import at.tatami.domain.model.Club
import at.tatami.domain.model.Person
import at.tatami.domain.repository.ClubRepository
import at.tatami.domain.repository.PersonRepository
import at.tatami.domain.repository.SelectedClubRepository
import at.tatami.domain.repository.SelectedPersonRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.withTimeout
import at.tatami.common.util.DataSourceLogger

/**
 * Centralized service for managing selected person and club state.
 * Provides shared, cached access to the currently selected entities
 * with single Firestore listeners shared across the entire application.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SelectedEntityService(
    private val selectedPersonRepository: SelectedPersonRepository,
    private val selectedClubRepository: SelectedClubRepository,
    private val personRepository: PersonRepository,
    private val clubRepository: ClubRepository,
    scope: CoroutineScope
) {
    
    /**
     * Shared StateFlow for the currently selected person.
     * Uses a single Firestore listener that's shared across all observers.
     * Automatically updates when the selection changes or the person data changes.
     */
    val selectedPerson: StateFlow<Person?> = selectedPersonRepository
        .observeSelectedPersonId()
        .flatMapLatest { personId ->
            if (personId != null) {
                personRepository.observePerson(personId)
                    .catch { e ->
                        DataSourceLogger.logNoData("Person (observe)", "Error observing person $personId: ${e.message}")
                        // Clear invalid selection
                        selectedPersonRepository.clearSelectedPerson()
                        emit(null)
                    }
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    
    /**
     * Shared StateFlow for the currently selected club.
     * Uses a single Firestore listener that's shared across all observers.
     * Automatically updates when the selection changes or the club data changes.
     */
    val selectedClub: StateFlow<Club?> = selectedClubRepository
        .observeSelectedClubId()
        .flatMapLatest { clubId ->
            if (clubId != null) {
                clubRepository.observeClub(clubId)
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    
    /**
     * Observes the currently selected person.
     * This is a shared flow - multiple observers will share the same upstream listener.
     */
    fun observeSelectedPerson(): Flow<Person?> = selectedPerson
    
    /**
     * Observes the currently selected club.
     * This is a shared flow - multiple observers will share the same upstream listener.
     */
    fun observeSelectedClub(): Flow<Club?> = selectedClub
    
    /**
     * Gets the current selected person value without observing changes.
     * Returns the cached value immediately.
     */
    fun getCurrentSelectedPerson(): Person? {
        val person = selectedPerson.value
        if (person != null) {
            DataSourceLogger.logCacheHit("Person", person.id)
        } else {
            DataSourceLogger.logCacheMiss("Person", "not available")
        }
        return person
    }

    /**
     * Suspends until a selected person is available and returns it.
     * This ensures the person data is fully loaded from Firestore.
     * Returns null if no person is selected after a 5-second timeout.
     */
    suspend fun awaitSelectedPerson(): Person? {
        return try {
            DataSourceLogger.logAwaitingFirestore("Person")
            withTimeout(5000) {
                selectedPerson
                    .filterNotNull()
                    .first()
                    .also { person ->
                        DataSourceLogger.logFirestoreFetch("Person", person.id)
                    }
            }
        } catch (_: Exception) {
            DataSourceLogger.logNoData("Person", "timeout or no selection")
            selectedPerson.value
        }
    }

    /**
     * Gets the current selected club value without observing changes.
     * Returns the cached value immediately.
     */
    fun getCurrentSelectedClub(): Club? {
        val club = selectedClub.value
        if (club != null) {
            DataSourceLogger.logCacheHit("Club", club.id)
        } else {
            DataSourceLogger.logCacheMiss("Club", "not available")
        }
        return club
    }
    
    /**
     * Suspends until a selected club is available and returns it.
     * This ensures the club data is fully loaded from Firestore.
     * Returns null if no club is selected after a 5-second timeout.
     */
    suspend fun awaitSelectedClub(): Club? {
        return try {
            DataSourceLogger.logAwaitingFirestore("Club")
            withTimeout(5000) {
                selectedClub
                    .filterNotNull()
                    .first()
                    .also { club ->
                        DataSourceLogger.logFirestoreFetch("Club", club.id)
                    }
            }
        } catch (_: Exception) {
            DataSourceLogger.logNoData("Club", "timeout or no selection")
            selectedClub.value
        }
    }
    
    /**
     * Prefetches person data to warm the cache.
     * This is automatically called when a person selection is made
     * to reduce perceived latency on subsequent access.
     */
    suspend fun prefetchPerson(personId: String?) {
        if (personId != null) {
            try {
                DataSourceLogger.logAwaitingFirestore("Person (prefetch)")
                personRepository.observePerson(personId).first()
                DataSourceLogger.logFirestoreFetch("Person (prefetch)", personId)
            } catch (_: Exception) {
                DataSourceLogger.logNoData("Person (prefetch)", "failed to prefetch $personId")
            }
        }
    }
    
    /**
     * Prefetches club data to warm the cache.
     * This is automatically called when a club selection is made
     * to reduce perceived latency on subsequent access.
     */
    suspend fun prefetchClub(clubId: String?) {
        if (clubId != null) {
            try {
                DataSourceLogger.logAwaitingFirestore("Club (prefetch)")
                clubRepository.observeClub(clubId).first()
                DataSourceLogger.logFirestoreFetch("Club (prefetch)", clubId)
            } catch (_: Exception) {
                DataSourceLogger.logNoData("Club (prefetch)", "failed to prefetch $clubId")
            }
        }
    }
    
    /**
     * Validates the current selections and clears any invalid ones.
     * This should be called on app startup to clean up orphaned selections.
     */
    suspend fun validateSelections() {
        try {
            // Validate person selection
            val personId = selectedPersonRepository.getSelectedPersonId()
            if (personId != null) {
                val person = personRepository.getPersonById(personId)
                if (person == null) {
                    DataSourceLogger.logNoData("Person (validation)", "clearing invalid selection: $personId")
                    selectedPersonRepository.clearSelectedPerson()
                }
            }
            
            // Validate club selection
            val clubId = selectedClubRepository.getSelectedClubId()
            if (clubId != null) {
                val club = clubRepository.getClubById(clubId)
                if (club == null) {
                    DataSourceLogger.logNoData("Club (validation)", "clearing invalid selection: $clubId")
                    selectedClubRepository.clearSelectedClub()
                }
            }
        } catch (_: Exception) {
            // If validation fails, don't clear selections - might be network issue
            DataSourceLogger.logNoData("Selection validation", "failed to validate selections")
        }
    }
}