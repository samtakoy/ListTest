package ru.samtakoy.listtest.app.di

import dagger.Binds
import dagger.Module
import ru.samtakoy.listtest.domain.model.cache.CacheModel
import ru.samtakoy.listtest.domain.model.cache.CacheModelImpl

@Module(includes = [ApiModule::class])
abstract class DomainModule {


    @Binds
    //abstract fun provideEmployeeInteractor(interactor: EmployeeInteractorImpl): EmployeeInteractor
    abstract fun provideCacheModel(cacheModel: CacheModelImpl): CacheModel
}