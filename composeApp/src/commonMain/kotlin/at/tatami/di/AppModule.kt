package at.tatami.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import dev.gitlive.firebase.storage.storage
import org.koin.dsl.module

/**
 * Firebase initialization module
 */
val firebaseModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage }
    single { Firebase.functions(region = "europe-west3") }
}

/**
 * Aggregated list of all application DI modules.
 *
 * Module organization:
 * - firebaseModule: Firebase SDK instances
 * - commonModule: Shared services, managers, and cross-cutting concerns
 * - authModule: Authentication feature (login, register, password reset)
 * - personModule: Person management feature (CRUD, profile images)
 * - clubModule: Club management feature (create, join, members)
 * - eventModule: Event management feature (create, view, respond)
 * - groupModule: Group management feature (create, view, member/trainer selection)
 * - trainingModule: Training management feature (create, view, filter trainings)
 * - settingsModule: Settings feature (theme, date/time, club settings)
 */
val appModules = listOf(
    firebaseModule,
    commonModule,
    authModule,
    personModule,
    clubModule,
    eventModule,
    groupModule,
    trainingModule,
    settingsModule
)