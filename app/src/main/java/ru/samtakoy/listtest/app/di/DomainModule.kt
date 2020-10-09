package ru.samtakoy.listtest.app.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.samtakoy.listtest.domain.TimestampHolder
import ru.samtakoy.listtest.domain.TimestampHolderImpl
import ru.samtakoy.listtest.domain.model.cache.CacheModel
import ru.samtakoy.listtest.domain.model.cache.impl.CacheModelMediatorImpl
import ru.samtakoy.listtest.domain.model.cache.CacheSettings
import javax.inject.Singleton

@Module(includes = [ApiModule::class])
abstract class DomainModule {

    @Binds
    @Singleton
    abstract fun bindTimestampHolder(arg: TimestampHolderImpl): TimestampHolder

    @Binds
    @Singleton
    abstract fun provideCacheModel(cacheModel: CacheModelMediatorImpl): CacheModel

    @Module
    companion object{

        @Provides
        @Singleton
        fun provideCacheSettings() = CacheSettings(60*60*24)
    }

}