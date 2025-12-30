package at.tatami.di

import at.tatami.common.data.service.FcmServiceImpl
import at.tatami.common.domain.service.FcmService
import at.tatami.data.datastore.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { createDataStore(androidContext()) }
    single<FcmService> { FcmServiceImpl(androidContext()) }
}