package com.android.eventapplication.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.eventapplication.EventViewModel
import com.android.eventapplication.ViewModelFactory
import com.android.eventapplication.autocomplete.view_model.AutoCompleteViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel::class)
    internal abstract fun bindEventViewModel(eventViewModel: EventViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AutoCompleteViewModel::class)
    internal abstract fun bindAutoCompleteViewModel(autoCompleteViewModel: AutoCompleteViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}