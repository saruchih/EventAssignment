package com.android.eventapplication.autocomplete.view

import com.android.eventapplication.autocomplete.model.preditictions.Prediction

interface OnPlaceSelectedListner {
    fun onPlaceSelected(location: Prediction)
}