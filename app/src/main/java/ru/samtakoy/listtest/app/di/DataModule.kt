package ru.samtakoy.listtest.app.di

import ru.samtakoy.listtest.data.local.cache.EmployeeCacheRepositoryImpl
import ru.samtakoy.listtest.data.remote.RemoteEmployeeRepositoryImpl
import ru.samtakoy.listtest.domain.reps.EmployeeCacheRepository
import ru.samtakoy.listtest.domain.reps.RemoteEmployeeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [ApiModule::class])
abstract class DataModule {

    @Binds
    abstract fun bindEmployeeRepository(rep: EmployeeCacheRepositoryImpl): EmployeeCacheRepository

    @Binds
    abstract fun bindRemoteEmployeeRepository(rep: RemoteEmployeeRepositoryImpl): RemoteEmployeeRepository

}