package at.tatami.group.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.ObserveIsCurrentPersonAdminUseCase
import at.tatami.domain.model.Group
import at.tatami.group.domain.usecase.ObserveGroupsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Groups list screen.
 * Displays groups with member/trainer counts and shows admin-only FAB.
 */
class GroupViewModel(
    private val observeGroupsUseCase: ObserveGroupsUseCase,
    private val observeIsCurrentPersonAdminUseCase: ObserveIsCurrentPersonAdminUseCase
) : ViewModel() {

    /**
     * Reactive list of groups with display-ready data.
     * Already filtered based on user permissions (admin vs member).
     */
    val groups: StateFlow<List<GroupDisplayItem>> = observeGroupsUseCase()
        .map { groups ->
            groups.map { group ->
                GroupDisplayItem(
                    group = group,
                    memberCount = group.memberIds.size,
                    trainerCount = group.trainerIds.size
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Whether the current user is an admin of the selected club.
     * Used to show/hide the "Create Group" FAB.
     */
    val isAdmin: StateFlow<Boolean> = observeIsCurrentPersonAdminUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * Display model for a group with pre-calculated counts.
     */
    data class GroupDisplayItem(
        val group: Group,
        val memberCount: Int,
        val trainerCount: Int
    )
}