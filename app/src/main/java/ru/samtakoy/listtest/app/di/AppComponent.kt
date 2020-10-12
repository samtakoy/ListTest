package ru.samtakoy.listtest.app.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.samtakoy.listtest.presentation.details.DetailsFragment
import ru.samtakoy.listtest.presentation.details_pager.DetailsPagerFragment
import ru.samtakoy.listtest.presentation.list.ListFragment
import ru.samtakoy.listtest.presentation.settings.SettingsFragment
import javax.inject.Singleton


@Component(modules = [
    SettingsModule::class,
    ApiModule::class,
    DataModule::class,
    DomainModule::class])
@Singleton
interface AppComponent {


    fun inject(f: ListFragment)
    fun inject(f: SettingsFragment)
    fun inject(f: DetailsFragment)
    fun inject(f: DetailsPagerFragment)

    @Component.Builder
    interface Builder{

        fun build():AppComponent
        @BindsInstance
        fun setContext(context: Context): Builder
    }

}