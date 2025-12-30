package at.tatami.di

import at.tatami.data.repository.DateTimeFormatRepositoryImpl
import at.tatami.data.repository.ThemeRepositoryImpl
import at.tatami.settings.domain.repository.DateTimeFormatRepository
import at.tatami.settings.domain.repository.ThemeRepository
import at.tatami.settings.domain.usecase.GetDateTimeFormatSettingsUseCase
import at.tatami.settings.domain.usecase.LoadThemeSettingsUseCase
import at.tatami.settings.domain.usecase.SaveDateTimeFormatSettingsUseCase
import at.tatami.settings.domain.usecase.SaveThemeSettingsUseCase
import at.tatami.settings.presentation.SettingsViewModel
import at.tatami.settings.presentation.account.AccountSettingsViewModel
import at.tatami.settings.presentation.club.ClubSettingsViewModel
import at.tatami.settings.presentation.system.SystemSettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI module for settings feature (theme, date/time format, account, club settings)
 */
val settingsModule = module {
    // Repositories
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<DateTimeFormatRepository> { DateTimeFormatRepositoryImpl(get()) }

    // Use cases
    factory { SaveThemeSettingsUseCase(get(), get()) }
    factory { LoadThemeSettingsUseCase(get(), get()) }
    factory { SaveDateTimeFormatSettingsUseCase(get()) }
    factory { GetDateTimeFormatSettingsUseCase(get()) }

    // ViewModels
    // Simplified main settings ViewModel (only admin check)
    viewModel { SettingsViewModel(get()) }

    // System settings ViewModel (theme + date/time formats)
    viewModel { SystemSettingsViewModel(get(), get(), get(), get()) }

    // Account settings ViewModel (sign out)
    viewModel { AccountSettingsViewModel(get(), get()) }

    // Club settings ViewModel (invite code management + delete club)
    viewModel { ClubSettingsViewModel(get(), get(), get(), get(), get(), get()) }
}