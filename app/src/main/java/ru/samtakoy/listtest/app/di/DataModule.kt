package ru.samtakoy.listtest.app.di

import ru.samtakoy.listtest.data.local.cache.EmployeeCacheRepositoryImpl
import ru.samtakoy.listtest.data.remote.RemoteEmployeeRepositoryImpl
import ru.samtakoy.listtest.domain.reps.EmployeeCacheRepository
import ru.samtakoy.listtest.domain.reps.RemoteEmployeeRepository
import dagger.Binds
import dagger.Module
import ru.samtakoy.listtest.data.local.AppPreferencesImpl
import ru.samtakoy.listtest.domain.Locals
import javax.inject.Singleton

@Module(includes = [ApiModule::class])
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindEmployeeRepository(rep: EmployeeCacheRepositoryImpl): EmployeeCacheRepository

    @Binds
    @Singleton
    abstract fun bindRemoteEmployeeRepository(rep: RemoteEmployeeRepositoryImpl): RemoteEmployeeRepository

    @Binds
    @Singleton
    abstract fun provideLocals(pref: AppPreferencesImpl): Locals
}