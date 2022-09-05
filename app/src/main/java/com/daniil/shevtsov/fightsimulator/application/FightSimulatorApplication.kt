package com.daniil.shevtsov.fightsimulator.application

import android.app.Application
import com.daniil.shevtsov.fightsimulator.common.di.initKoin
import com.daniil.shevtsov.fightsimulator.core.di.DaggerAppComponent
import com.daniil.shevtsov.fightsimulator.core.di.koin.appModule
import com.daniil.shevtsov.fightsimulator.feature.coreshell.domain.gameState
import org.koin.core.Koin
import timber.log.Timber
import javax.inject.Inject

class FightSimulatorApplication : Application() {
    lateinit var koin: Koin
    val appComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(
                appContext = applicationContext,
                initialGameState = gameState()
            )
    }

    @Inject
    lateinit var viewModel: ApplicationViewModel


    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        koin = initKoin {
            modules(appModule)
        }.koin

        appComponent.inject(this)

        viewModel.onStart()
    }

    override fun onTerminate() {
        viewModel.onCleared()
        super.onTerminate()
    }

}
