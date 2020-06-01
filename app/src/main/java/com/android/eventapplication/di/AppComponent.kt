package com.android.eventapplication.di

import com.android.eventapplication.MapsActivity
import com.android.eventapplication.autocomplete.view.AutoCompleteFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(mapsActivity: MapsActivity)
    fun inject(autoCompleteFragment: AutoCompleteFragment)
}