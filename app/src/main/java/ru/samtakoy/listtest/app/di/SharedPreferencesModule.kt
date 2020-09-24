package ru.samtakoy.listtest.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.samtakoy.listtest.data.local.AppPreferencesImpl
import ru.samtakoy.listtest.domain.Locals

@Module
class SharedPreferencesModule(private val context: Context) {

    @Provides
    fun provideLocals(): Locals = AppPreferencesImpl(context)

}