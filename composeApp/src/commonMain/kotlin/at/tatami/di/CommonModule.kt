package at.tatami.di

import at.tatami.common.data.service.FcmServiceImpl
import at.tatami.common.domain.manager.ThemeManager
import at.tatami.common.domain.manager.UserDataCleanupManager
import at.tatami.common.domain.manager.UserDataCleanupManagerImpl
import at.tatami.common.domain.service.DateTimeFormatterService
import at.tatami.common.domain.service.FcmService
import at.tatami.common.domain.service.SelectedEntityService
import at.tatami.common.domain.usecase.ValidateSelectionsUseCase
import at.tatami.common.util.LocaleProvider
import at.tatami.core.StorageService
import at.tatami.data.repository.UserRepositoryImpl
import at.tatami.domain.repository.UserRepository
import at.tatami.main.presentation.scaffold.MainScaffoldViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * DI module for common/shared components used across features
 */
val commonModule = module {
    // Application scope for long-running operations
    single<CoroutineScope>(named("ApplicationScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("ApplicationScope"))
    }

    // Shared services and managers
    single { ThemeManager() }
    single { LocaleProvider() }
    single { StorageService(get()) }
    single<UserDataCleanupManager> { UserDataCleanupManagerImpl(get(), get()) }
    single { DateTimeFormatterService(get(), get()) }
    single { SelectedEntityService(get(), get(), get(), get(), get(named("ApplicationScope"))) }

    // User repository (used across features)
    single<UserRepository> { UserRepositoryImpl(get(), get(), get(), get()) }

    // Cross-cutting use cases
    factory { ValidateSelectionsUseCase(get()) }

    // Main scaffold ViewModel
    viewModel { MainScaffoldViewModel(get()) }
}