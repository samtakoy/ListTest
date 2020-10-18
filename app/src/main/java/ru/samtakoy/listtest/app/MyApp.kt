package ru.samtakoy.listtest.app

import android.app.Application

class MyApp : Application(){

    override fun onCreate() {
        super.onCreate()

        Di.createComponent(applicationContext)
    }
}