package ru.samtakoy.listtest.app.di

import android.content.Context
import dagger.BindsInstance
import ru.samtakoy.listtest.presentation.list.ListFragment
import dagger.Component
import javax.inject.Singleton


@Component(modules = [
    SettingsModule::class,
    ApiModule::class,
    DataModule::class,
    DomainModule::class])
@Singleton
interface AppComponent {


    fun inject(f: ListFragment)

    @Component.Builder
    interface Builder{

        fun build():AppComponent
        @BindsInstance
        fun setContext(context: Context): Builder
    }

}