package at.tatami.di

import at.tatami.data.repository.GroupRepositoryImpl
import at.tatami.domain.repository.GroupRepository
import at.tatami.group.domain.usecase.CreateGroupUseCase
import at.tatami.group.domain.usecase.DeleteGroupUseCase
import at.tatami.group.domain.usecase.GetAttendanceStatisticsUseCase
import at.tatami.group.domain.usecase.GetGroupByIdUseCase
import at.tatami.group.domain.usecase.ObserveGroupByIdUseCase
import at.tatami.group.domain.usecase.ObserveGroupsUseCase
import at.tatami.group.domain.usecase.UpdateGroupMembersUseCase
import at.tatami.group.domain.usecase.UpdateGroupNameUseCase
import at.tatami.group.domain.usecase.UpdateGroupTrainersUseCase
import at.tatami.group.presentation.GroupViewModel
import at.tatami.group.presentation.create.GroupCreateViewModel
import at.tatami.group.presentation.detail.GroupDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin DI module for group management feature.
 * Provides repositories, use cases, and ViewModels for groups.
 */
val groupModule = module {
    // Repository
    single<GroupRepository> { GroupRepositoryImpl(get(), get()) }

    // Use Cases
    factory { CreateGroupUseCase(get(), get(), get()) }
    factory { ObserveGroupsUseCase(get(), get(), get()) }
    factory { GetGroupByIdUseCase(get(), get()) }
    factory { ObserveGroupByIdUseCase(get(), get()) }
    factory { DeleteGroupUseCase(get(), get(), get()) }
    factory { UpdateGroupMembersUseCase(get(), get()) }
    factory { UpdateGroupTrainersUseCase(get(), get()) }
    factory { UpdateGroupNameUseCase(get(), get()) }
    factory { GetAttendanceStatisticsUseCase(get(), get(), get()) }

    // ViewModels
    viewModel { GroupViewModel(get(), get()) }
    viewModel { GroupCreateViewModel(get(), get(), get(), get()) }
    viewModel { GroupDetailViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}