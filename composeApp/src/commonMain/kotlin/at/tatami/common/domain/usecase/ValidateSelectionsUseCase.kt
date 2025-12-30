package at.tatami.common.domain.usecase

import at.tatami.common.domain.service.SelectedEntityService

/**
 * Use case for validating that the currently selected person and club are valid.
 * This ensures data integrity when:
 * - The app starts up
 * - User signs in
 * - User switches accounts
 * 
 * If selected entities are invalid (don't exist), they will be cleared from storage.
 * This use case validates both person and club selections in a single call.
 */
class ValidateSelectionsUseCase(
    private val selectedEntityService: SelectedEntityService
) {
    /**
     * Validates both person and club selections.
     * Clears any invalid selections that point to non-existent entities.
     */
    suspend operator fun invoke() {
        selectedEntityService.validateSelections()
    }
}