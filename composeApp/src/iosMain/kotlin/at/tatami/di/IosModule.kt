package at.tatami.di

import at.tatami.common.data.service.FcmServiceImpl
import at.tatami.common.domain.service.FcmService
import at.tatami.data.datastore.createDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module

val iosModule = module {
    single<DataStore<Preferences>> { createDataStore() }
    single<FcmService> { FcmServiceImpl() }
}
