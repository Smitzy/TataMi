package at.tatami.di

import at.tatami.data.repository.EventRepositoryImpl
import at.tatami.data.repository.NotificationRepositoryImpl
import at.tatami.domain.repository.EventRepository
import at.tatami.domain.repository.NotificationRepository
import at.tatami.event.domain.usecase.CreateEventUseCase
import at.tatami.event.domain.usecase.DeleteEventUseCase
import at.tatami.event.domain.usecase.GetEventByIdUseCase
import at.tatami.event.domain.usecase.ObservePastEventsUseCase
import at.tatami.event.domain.usecase.ObserveUpcomingEventsUseCase
import at.tatami.event.domain.usecase.UpdateEventDescriptionUseCase
import at.tatami.event.domain.usecase.UpdateEventLocationUseCase
import at.tatami.event.domain.usecase.UpdateEventStartDateTimeUseCase
import at.tatami.event.domain.usecase.UpdateEventStatusUseCase
import at.tatami.event.domain.usecase.UpdateEventTitleUseCase
import at.tatami.event.presentation.create.EventCreateViewModel
import at.tatami.event.presentation.detail.EventDetailViewModel
import at.tatami.event.presentation.EventViewModel
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.person.domain.usecase.GetPersonByIdUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI module for event management feature
 */
val eventModule = module {
    // Repositories
    single<EventRepository> { EventRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }

    // Use cases
    factory { CreateEventUseCase(get(), get(), get(), get()) }
    factory { ObserveUpcomingEventsUseCase(get(), get(), get()) }
    factory { ObservePastEventsUseCase(get(), get(), get()) }
    factory { UpdateEventStatusUseCase(get(), get(), get()) }
    factory { GetEventByIdUseCase(get(), get()) }
    factory { UpdateEventTitleUseCase(get(), get()) }
    factory { UpdateEventDescriptionUseCase(get(), get()) }
    factory { UpdateEventLocationUseCase(get(), get()) }
    factory { UpdateEventStartDateTimeUseCase(get(), get()) }
    factory { DeleteEventUseCase(get(), get()) }

    // ViewModels
    viewModel { EventViewModel(get(), get(), get(), get(), get()) }
    viewModel { EventCreateViewModel(get(), get(), get(), get()) }
    viewModel {
        EventDetailViewModel(
            get(), get(), get(), get<GetSelectedPersonUseCase>(), get(),
            get<ObserveSelectedClubUseCase>(), get<ObserveSelectedPersonUseCase>(),
            get(), get(), get(), get(), get()
        )
    }
}