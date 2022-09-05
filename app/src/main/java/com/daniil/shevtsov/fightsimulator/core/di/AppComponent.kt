package com.daniil.shevtsov.fightsimulator.core.di

import android.content.Context
import com.daniil.shevtsov.fightsimulator.application.FightSimulatorApplication
import com.daniil.shevtsov.fightsimulator.core.navigation.ScreenHostFragment
import com.daniil.shevtsov.fightsimulator.feature.coreshell.domain.GameState
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(
    modules = [
        AppModule::class,
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance appContext: Context,
            @BindsInstance initialGameState: GameState,
        ): AppComponent
    }

    fun inject(screenHostFragment: ScreenHostFragment)
    fun inject(application: FightSimulatorApplication)
}
