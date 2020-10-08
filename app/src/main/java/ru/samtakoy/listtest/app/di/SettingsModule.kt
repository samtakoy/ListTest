package ru.samtakoy.listtest.app.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import ru.samtakoy.listtest.app.misc.AppCoroutineDispatchers
import javax.inject.Singleton

@Module
abstract class SettingsModule (){

    @Module
    companion object{

        @Provides
        @Singleton
        fun provideAppCoroutineDispatchers() = AppCoroutineDispatchers(
            //Executors.newSingleThreadExecutor().asCoroutineDispatcher(),
            newSingleThreadContext("databaseThread"),
            Dispatchers.IO,
            Dispatchers.IO,
            Dispatchers.Main

        )

    }

}