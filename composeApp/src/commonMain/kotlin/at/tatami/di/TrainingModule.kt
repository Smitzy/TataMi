package at.tatami.di

import at.tatami.data.repository.TrainingRepositoryImpl
import at.tatami.domain.repository.TrainingRepository
import at.tatami.training.domain.usecase.CreateTrainingUseCase
import at.tatami.training.domain.usecase.DeleteTrainingUseCase
import at.tatami.training.domain.usecase.ObserveCanCreateTrainingUseCase
import at.tatami.training.domain.usecase.ObserveCanEditTrainingUseCase
import at.tatami.training.domain.usecase.ObservePastTrainingsUseCase
import at.tatami.training.domain.usecase.ObserveTrainingByIdUseCase
import at.tatami.training.domain.usecase.ObserveUpcomingTrainingsUseCase
import at.tatami.training.domain.usecase.UpdateTrainingAttendanceUseCase
import at.tatami.training.domain.usecase.UpdateTrainingNotesUseCase
import at.tatami.training.presentation.TrainingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin dependency injection module for training management feature.
 * Provides repositories, use cases, and ViewModels for trainings.
 */
val trainingModule = module {
    // Repository
    single<TrainingRepository> { TrainingRepositoryImpl(get()) }

    // Use Cases
    factory { CreateTrainingUseCase(get(), get()) }
    factory { ObserveUpcomingTrainingsUseCase(get(), get()) }
    factory { ObservePastTrainingsUseCase(get(), get()) }
    factory { ObserveCanCreateTrainingUseCase(get(), get(), get()) }
    factory { ObserveTrainingByIdUseCase(get(), get()) }
    factory { UpdateTrainingNotesUseCase(get(), get()) }
    factory { UpdateTrainingAttendanceUseCase(get(), get()) }
    factory { DeleteTrainingUseCase(get(), get()) }
    factory { ObserveCanEditTrainingUseCase(get(), get(), get()) }

    // TrainingViewModel with groupId parameter
    viewModel { (groupId: String) ->
        TrainingViewModel(
            groupId = groupId,
            observeUpcomingTrainingsUseCase = get(),
            observePastTrainingsUseCase = get(),
            observeCanCreateTrainingUseCase = get(),
            createTrainingUseCase = get(),
            dateTimeFormatter = get()
        )
    }

    // TrainingDetailViewModel with groupId and trainingId parameters
    viewModel { (groupId: String, trainingId: String) ->
        at.tatami.training.presentation.detail.TrainingDetailViewModel(
            groupId = groupId,
            trainingId = trainingId,
            observeTrainingByIdUseCase = get(),
            observeCanEditTrainingUseCase = get(),
            updateTrainingNotesUseCase = get(),
            updateTrainingAttendanceUseCase = get(),
            deleteTrainingUseCase = get(),
            getGroupByIdUseCase = get(),
            getPersonByIdUseCase = get(),
            observeSelectedPersonUseCase = get(),
            dateTimeFormatter = get()
        )
    }
}