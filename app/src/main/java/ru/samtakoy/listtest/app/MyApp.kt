package ru.samtakoy.listtest.app

import android.app.Application
import ru.samtakoy.listtest.app.Di
import ru.samtakoy.listtest.app.di.DaggerAppComponent

class MyApp : Application(){

    override fun onCreate() {
        super.onCreate()

        Di.appComponent = DaggerAppComponent.builder()
            .setContext(applicationContext)
            .build()
    }
}