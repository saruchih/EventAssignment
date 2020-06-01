package com.android.eventapplication.networking

import com.android.eventapplication.autocomplete.model.placedetails.PlaceDetailsResponse
import com.android.eventapplication.autocomplete.model.preditictions.PlacesResponse
import com.android.eventapplication.model.EventModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {

    @GET("json/events/search")
    suspend fun getEvents(@QueryMap param: Map<String, String>): Response<EventModel>

    @GET()
    suspend fun getAutoCompletePerditictions(@Url url: String): Response<PlacesResponse>

    @GET()
    suspend fun getPlaceDetail(@Url url: String): Response<PlaceDetailsResponse>

}