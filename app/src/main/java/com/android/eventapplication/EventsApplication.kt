package com.android.eventapplication

import android.app.Application
import com.android.eventapplication.di.AppComponent
import com.android.eventapplication.di.DaggerAppComponent

class EventsApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger()
    }

    private fun initDagger(): AppComponent {

        return DaggerAppComponent.builder().build()

    }
}