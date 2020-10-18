package ru.samtakoy.listtest.app

import android.content.Context
import ru.samtakoy.listtest.app.di.AppComponent
import ru.samtakoy.listtest.app.di.DaggerAppComponent

object Di {

    lateinit var appComponent: AppComponent

    fun createComponent(context: Context){
        appComponent = DaggerAppComponent.builder()
            .setContext(context)
            .build()
    }

}