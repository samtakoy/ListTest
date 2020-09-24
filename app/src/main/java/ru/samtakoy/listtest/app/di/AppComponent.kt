package ru.samtakoy.listtest.app.di

import ru.samtakoy.listtest.presentation.list.ListFragment
import dagger.Component
import javax.inject.Singleton


@Component(modules = [
    SharedPreferencesModule::class,
    ApiModule::class,
    DataModule::class,
    DomainModule::class])
@Singleton
interface AppComponent {


    fun inject(f: ListFragment)

}