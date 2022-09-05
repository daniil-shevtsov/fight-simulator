package com.daniil.shevtsov.fightsimulator.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daniil.shevtsov.fightsimulator.core.di.AppScope
import com.daniil.shevtsov.fightsimulator.core.navigation.ScreenHostViewModel
import com.daniil.shevtsov.fightsimulator.prototypes.fight.ui.FightImperativeShell
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ScreenHostViewModel::class)
    fun screenHostViewModel(viewModel: ScreenHostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FightImperativeShell::class)
    fun fightImperativeShell(fightImperativeShell: FightImperativeShell): ViewModel

    @Binds
    @AppScope
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
