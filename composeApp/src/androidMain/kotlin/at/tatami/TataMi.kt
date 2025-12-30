package at.tatami

import android.app.Application
import at.tatami.di.androidModule
import at.tatami.di.initKoin
import org.koin.android.ext.koin.androidContext

class TataMi : Application()  {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TataMi)
            modules(androidModule)
        }
    }
}