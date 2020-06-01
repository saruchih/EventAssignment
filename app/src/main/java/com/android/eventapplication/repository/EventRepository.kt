package com.android.eventapplication.repository

import com.android.eventapplication.autocomplete.model.placedetails.PlaceDetailsResponse
import com.android.eventapplication.autocomplete.model.preditictions.PlacesResponse
import com.android.eventapplication.model.EventModel
import com.android.eventapplication.networking.ApiService
import com.android.eventapplication.networking.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(private val service: ApiService) {
    /**
     * fetch the event list
     */
    suspend fun getEventData(map: Map<String, String>): Result<EventModel?> =
        withContext(Dispatchers.IO) {

            val request = async { service.getEvents(map) }
            try {
                val response = request.await()
                when {
                    response.isSuccessful -> Result.Success(
                        (response.body())
                    )
                    else
                    -> Result.Error(RemoteDataNotFoundException())
                }
            } catch (ex: Throwable) {
                Result.Error(ex)
            }

        }

    /**
     * fetch the predictions
     */
    suspend fun getAutoCompletePerditictions(url: String): Result<PlacesResponse?> =
        withContext(Dispatchers.IO) {

            val request = async { service.getAutoCompletePerditictions(url) }
            try {
                val response = request.await()
                when {

                    response.isSuccessful -> Result.Success(
                        (response.body())
                    )
                    else
                    -> Result.Error(DataSourceException(response.body()?.status))
                }
            } catch (ex: Throwable) {
                Result.Error(ex)
            }

        }

    /**
     * fetch the place details
     */
    suspend fun getPlaceDetails(url: String): Result<PlaceDetailsResponse?> =
        withContext(Dispatchers.IO) {

            val request = async { service.getPlaceDetail(url) }
            try {
                val response = request.await()
                when {

                    response.isSuccessful -> Result.Success(
                        (response.body())
                    )
                    else
                    -> Result.Error(DataSourceException(response.body()?.status))
                }
            } catch (ex: Throwable) {
                Result.Error(ex)
            }

        }

}