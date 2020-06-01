package com.android.eventapplication.autocomplete.model.preditictions

data class PlacesResponse(
    val error_message: String,
    val predictions: List<Prediction>,
    val status: String
)