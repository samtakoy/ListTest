package ru.samtakoy.listtest

import android.app.Application
import ru.samtakoy.listtest.app.Di
import ru.samtakoy.listtest.app.di.ApiModule
import ru.samtakoy.listtest.app.di.DaggerAppComponent
import ru.samtakoy.listtest.app.di.SharedPreferencesModule

class MyApp : Application(){

    override fun onCreate() {
        super.onCreate()

        Di.appComponent = DaggerAppComponent.builder()
            .apiModule(ApiModule(this))
            .sharedPreferencesModule(SharedPreferencesModule(this))
            .build()
    }
}