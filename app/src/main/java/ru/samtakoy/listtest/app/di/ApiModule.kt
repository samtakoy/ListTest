package ru.samtakoy.listtest.app.di

import android.content.Context
import androidx.room.Room
import ru.samtakoy.listtest.data.remote.api.RequestApi
import ru.samtakoy.listtest.data.remote.api.retrofit
import ru.samtakoy.listtest.data.local.cache.database.CacheDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApiModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideRequestApi(): RequestApi =
        retrofit.create(RequestApi::class.java)


    @Provides
    @Singleton
    fun provideDatabase(): CacheDatabase =
        Room.databaseBuilder(context, CacheDatabase::class.java, "employees_cache").build()

}