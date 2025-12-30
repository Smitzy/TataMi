package at.tatami.di

import at.tatami.data.repository.PersonRepositoryImpl
import at.tatami.data.repository.SelectedPersonRepositoryImpl
import at.tatami.domain.repository.PersonRepository
import at.tatami.domain.repository.SelectedPersonRepository
import at.tatami.person.domain.usecase.CreatePersonUseCase
import at.tatami.person.domain.usecase.GetPersonByIdUseCase
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import at.tatami.person.domain.usecase.ObservePersonUseCase
import at.tatami.person.domain.usecase.ObservePersonsByUserUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import at.tatami.person.domain.usecase.SetSelectedPersonUseCase
import at.tatami.person.domain.usecase.UpdatePersonUseCase
import at.tatami.person.domain.usecase.UploadPersonProfileImageUseCase
import at.tatami.person.presentation.create.CreatePersonViewModel
import at.tatami.person.presentation.edit.EditPersonViewModel
import at.tatami.person.presentation.photoupload.PersonPhotoUploadViewModel
import at.tatami.person.presentation.list.PersonListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI module for person management feature
 */
val personModule = module {
    // Repositories
    single<PersonRepository> { PersonRepositoryImpl(get(), get()) }
    single<SelectedPersonRepository> { SelectedPersonRepositoryImpl(get()) }

    // Use cases
    factory { ObservePersonsByUserUseCase(get()) }
    factory { CreatePersonUseCase(get(), get()) }
    factory { ObservePersonUseCase(get()) }
    factory { GetPersonByIdUseCase(get()) }
    factory { UpdatePersonUseCase(get()) }
    factory { UploadPersonProfileImageUseCase(get()) }
    factory { SetSelectedPersonUseCase(get(), get(), get()) }
    factory { ObserveSelectedPersonUseCase(get()) }
    factory { GetSelectedPersonUseCase(get()) }

    // ViewModels
    viewModel { PersonListViewModel(get(), get(), get(), get()) }
    viewModel { CreatePersonViewModel(get()) }
    viewModel { parameters ->
        PersonPhotoUploadViewModel(
            personId = parameters.get(),
            getPersonByIdUseCase = get(),
            uploadPersonProfileImageUseCase = get()
        )
    }
    viewModel { parameters ->
        EditPersonViewModel(
            personId = parameters.get(),
            getPersonByIdUseCase = get(),
            updatePersonUseCase = get(),
            uploadPersonProfileImageUseCase = get()
        )
    }
}