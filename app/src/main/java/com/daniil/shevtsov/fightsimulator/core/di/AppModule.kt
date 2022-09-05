package com.daniil.shevtsov.fightsimulator.core.di

import com.daniil.shevtsov.fightsimulator.core.di.viewmodel.ViewModelModule

import dagger.Module

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
interface AppModule {

}
