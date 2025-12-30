package at.tatami.di

import at.tatami.data.repository.ClubRepositoryImpl
import at.tatami.data.repository.SelectedClubRepositoryImpl
import at.tatami.domain.repository.ClubRepository
import at.tatami.domain.repository.SelectedClubRepository
import at.tatami.club.domain.usecase.CreateClubUseCase
import at.tatami.club.domain.usecase.DeleteClubUseCase
import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.club.domain.usecase.JoinClubUseCase
import at.tatami.club.domain.usecase.ObserveIsCurrentPersonAdminUseCase
import at.tatami.club.domain.usecase.ObservePersonClubsUseCase
import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.club.domain.usecase.SetSelectedClubUseCase
import at.tatami.club.domain.usecase.UploadClubProfileImageUseCase
import at.tatami.club.presentation.create.CreateClubViewModel
import at.tatami.club.presentation.photoupload.ClubPhotoUploadViewModel
import at.tatami.club.presentation.join.JoinClubViewModel
import at.tatami.club.presentation.list.ClubListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI module for club management feature
 */
val clubModule = module {
    // Repositories
    single<ClubRepository> { ClubRepositoryImpl(get(), get(), get(), get()) }
    single<SelectedClubRepository> { SelectedClubRepositoryImpl(get()) }

    // Use cases
    factory { CreateClubUseCase(get(), get(), get()) }
    factory { JoinClubUseCase(get()) }
    factory { ObservePersonClubsUseCase(get(), get()) }
    factory { SetSelectedClubUseCase(get(), get()) }
    factory { ObserveSelectedClubUseCase(get()) }
    factory { GetSelectedClubUseCase(get()) }
    factory { ObserveIsCurrentPersonAdminUseCase(get(), get()) }
    factory { UploadClubProfileImageUseCase(get()) }
    factory { DeleteClubUseCase(get(), get(), get(), get()) }

    // ViewModels
    viewModel { JoinClubViewModel(get(), get()) }
    viewModel { CreateClubViewModel(get()) }
    viewModel { (clubId: String) -> ClubPhotoUploadViewModel(clubId, get(), get()) }
    viewModel { ClubListViewModel(get(), get(), get()) }
}