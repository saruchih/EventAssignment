package com.android.eventapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.eventapplication.autocomplete.model.placedetails.PlaceDetailsResponse
import com.android.eventapplication.model.EventModel
import com.android.eventapplication.networking.Result
import com.android.eventapplication.repository.EventRepository
import com.android.eventapplication.util.CommonConstants
import kotlinx.coroutines.launch
import javax.inject.Inject

class EventViewModel @Inject constructor(var eventRepository: EventRepository) : ViewModel() {
    private val mSpinner = MutableLiveData<Boolean>()
    val spinner: LiveData<Boolean> = mSpinner
    private val message = MutableLiveData<String>()
    val errorMessage: LiveData<String> = message
    private val data = MutableLiveData<EventModel>()
    val response: LiveData<EventModel> = data
    private val placeDetail = MutableLiveData<PlaceDetailsResponse>()
    val placeDetailresponse: LiveData<PlaceDetailsResponse> = placeDetail

    fun getEventData(hashMap: HashMap<String, String>) {
        viewModelScope.launch {
            mSpinner.value = true
            when (val result = eventRepository.getEventData(hashMap)) {
                is Result.Success -> data.postValue(result.data)
                is Result.Error -> message.postValue(result.exception.message)
            }

            mSpinner.value = false
        }
    }

    /**
     * gets the place details
     * params = APIKey+PlaceId
     */
    fun getPlaceDetails(params: String) {
        viewModelScope.launch {
            mSpinner.value = true
            val result =
                eventRepository.getPlaceDetails(CommonConstants.PLACE_DETAIL_BASE_URL + params)

            when (result) {
                is Result.Success -> placeDetail.postValue(
                    result.data
                )
                is Result.Error -> message.postValue(result.exception.message)
            }

            mSpinner.value = false
        }
    }
}
