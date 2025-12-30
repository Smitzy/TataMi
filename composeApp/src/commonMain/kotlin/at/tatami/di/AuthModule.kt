package at.tatami.di

import at.tatami.data.repository.AuthRepositoryImpl
import at.tatami.domain.repository.AuthRepository
import at.tatami.auth.domain.usecase.CheckEmailVerificationUseCase
import at.tatami.auth.domain.usecase.GetCurrentUserUseCase
import at.tatami.auth.domain.usecase.ObserveAuthStateUseCase
import at.tatami.auth.domain.usecase.RegisterFcmTokenUseCase
import at.tatami.auth.domain.usecase.ResendVerificationEmailUseCase
import at.tatami.auth.domain.usecase.SendPasswordResetUseCase
import at.tatami.auth.domain.usecase.SignInUseCase
import at.tatami.auth.domain.usecase.SignOutUseCase
import at.tatami.auth.domain.usecase.SignUpUseCase
import at.tatami.auth.presentation.emailverification.EmailVerificationViewModel
import at.tatami.auth.presentation.forgotpassword.ForgotPasswordViewModel
import at.tatami.auth.presentation.login.LoginViewModel
import at.tatami.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI module for authentication feature
 */
val authModule = module {
    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // Use cases
    factory { SignInUseCase(get(), get()) }
    factory { SignUpUseCase(get(), get()) }
    factory { SignOutUseCase(get(), get(), get()) }
    factory { SendPasswordResetUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { ObserveAuthStateUseCase(get()) }
    factory { CheckEmailVerificationUseCase(get(), get()) }
    factory { ResendVerificationEmailUseCase(get()) }
    factory { RegisterFcmTokenUseCase(get(), get()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { EmailVerificationViewModel(get(), get(), get(), get()) }
}