package com.android.eventapplication.autocomplete.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.eventapplication.autocomplete.model.preditictions.PlacesResponse
import com.android.eventapplication.networking.Result
import com.android.eventapplication.repository.EventRepository
import com.android.eventapplication.util.CommonConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class AutoCompleteViewModel @Inject constructor(var eventRepository: EventRepository) :
    ViewModel() {
    private var searchJob: Job? = null
    private val mSpinner = MutableLiveData<Boolean>()
    val spinner: LiveData<Boolean> = mSpinner
    private val message = MutableLiveData<String>()
    val errorMessage: LiveData<String> = message
    private val data = MutableLiveData<PlacesResponse>()
    val placeResponse: LiveData<PlacesResponse> = data


    /**
     * viewmodelscope corouitne to hit the api
     * and get the preditiocns Result
     * params = APIKey+InputString
     */
    fun getAutoCompletePerditctions(params: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            mSpinner.value = true
            delay(2000)
            val result =
                eventRepository.getAutoCompletePerditictions(CommonConstants.PLACES_BASE_URL + params)
            if (isActive) {
                when (result) {
                    is Result.Success -> data.postValue(result.data)
                    is Result.Error -> message.postValue(result.exception.message)
                }
            }

            mSpinner.value = false
        }
    }

}