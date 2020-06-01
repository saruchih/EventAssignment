package com.android.eventapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.eventapplication.autocomplete.model.preditictions.Prediction
import com.android.eventapplication.autocomplete.view.AutoCompleteFragment
import com.android.eventapplication.autocomplete.view.OnPlaceSelectedListner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject


class MapsActivity : BaseActivity(), OnMapReadyCallback, OnPlaceSelectedListner {

    private lateinit var spinner: ProgressBar
    private lateinit var parentLayout: RelativeLayout
    private lateinit var mMap: GoogleMap
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: EventViewModel
    private var placeId: String? = null
    private var currentLatLng: LatLng? = null
    private var address: String? = null
    private lateinit var menu: Menu


    companion object {
        private const val PARAM_API_KEY = "app_key"
        private const val PARAM_LOCATION_KEY = "location"
        private const val PLACE_ID_PARAM = "place_id="
        private const val KEY_PARAM = "key="
        private const val KEY_WHERE_LOCATION = "where"
        private const val KEY_WHERE_LOCATION_WITH_IN = "within"
        private const val RADIUS = "25"
        private const val PARAM_FILTER_KEYWORD = "keywords"
        const val PARAM_FILTER_MUSIC = "music"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        (application as EventsApplication).appComponent.inject(this)
        parentLayout = findViewById(R.id.layout_parent)
        spinner = findViewById(R.id.progressBar)

        viewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)
        if (!checkIfPermissionGranted()) {
            parentLayout.snack(getString(R.string.error_permission_request))
            requestLocationPermissions()
        } else {
            setUpUIi()
        }


    }


    override fun setUpUIi() {
        initMap()
        showError()
        showPrgressbar()
        showResponse()
        showFilterResponse()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map, menu)
        if (menu != null) {
            this.menu = menu

        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val fragment = AutoCompleteFragment()
                fragmentTransaction.replace(R.id.container, fragment)
                fragmentTransaction.addToBackStack(fragment.javaClass.name)
                fragmentTransaction.commit()
            }

            R.id.action_filter -> {

                if (isConnctedToNetwork()) {
                    if (null != placeId) {
                        val apiKey = "$KEY_PARAM${getString(R.string.google_maps_key)}"
                        val parameters = "$PLACE_ID_PARAM$placeId&$apiKey"
                        viewModel.getPlaceDetails(parameters)
                    } else {
                        showToast(getString(R.string.error_select_valid_address))
                    }
                } else {
                    showToast(getString(R.string.error_code_offline))
                }


            }
            R.id.action_user_filter -> {

                if (isConnctedToNetwork()) {
                    if (null != currentLatLng) {
                        val loc = "${currentLatLng!!.latitude},${currentLatLng!!.longitude}"
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap[PARAM_API_KEY] = getString(R.string.eventful_api_key)
                        hashMap[KEY_WHERE_LOCATION] = loc
                        hashMap[KEY_WHERE_LOCATION_WITH_IN] = RADIUS
                        callEventsApi(hashMap)
                    } else {
                        showToast(getString(R.string.error_select_valid_address))
                    }
                } else {
                    showToast(getString(R.string.error_code_offline))
                }

            }
            R.id.action_music_filter -> {

                if (isConnctedToNetwork()) {
                    if (null != address) {
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap[PARAM_API_KEY] = getString(R.string.eventful_api_key)
                        hashMap[PARAM_LOCATION_KEY] = address!!
                        hashMap[PARAM_FILTER_KEYWORD] = PARAM_FILTER_MUSIC
                        this.callEventsApi(hashMap)
                    } else {
                        showToast(getString(R.string.error_select_valid_address))
                    }
                } else {
                    showToast(getString(R.string.error_code_offline))
                }


            }


            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    @SuppressLint("MissingPermission")
    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getDeviceLocation() {
        /*
 * Get the best and most recent location of the device, which may be null in rare
 * cases when a location is not available.
 */
        if (isConnctedToNetwork()) {

            try {
                val task = mFusedLocationProviderClient.lastLocation
                task.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val latLng = it.result?.latitude?.let { it1 ->
                            it.result?.longitude?.let { it2 ->
                                LatLng(
                                    it1,
                                    it2
                                )
                            }
                        }
                        this.currentLatLng = latLng
                        mMap.addMarker(
                            MarkerOptions().position(latLng!!).icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                            )
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM))
                    } else {
                        Log.d("TAG", "Current location is null. Using defaults.")
                        Log.e("TAG", "Exception: %s", task.exception)

                    }
                }
            } catch (exception: SecurityException) {
                Log.e("Exception: %s", exception.message.toString())
            }
        } else {
            showToast(getString(R.string.error_code_offline))
        }
    }


    override fun onPlaceSelected(location: Prediction) {
        placeId = location.place_id
        address = location.description
        val hashMap: HashMap<String, String> = HashMap()
        hashMap[PARAM_API_KEY] = getString(R.string.eventful_api_key)
        hashMap[PARAM_LOCATION_KEY] = location.description
        callEventsApi(hashMap)


    }


    private fun callEventsApi(hashMap: HashMap<String, String>) {
        if (isConnctedToNetwork()) {
            viewModel.getEventData(hashMap)
        } else {
            showToast(getString(R.string.error_code_offline))
        }
    }

    /**
     * display api error
     */
    override fun showError() {
        // show the error message
        viewModel.errorMessage.observe(this, Observer { text ->
            text?.let {
                parentLayout.snack(it)
            }
        })
    }

    /**
     * display progressbar
     */
    override fun showPrgressbar() {
        // Show spinner when loading from API
        viewModel.spinner.observe(this, Observer { value ->
            value?.let {
                if (it) {
                    showProgress(spinner)
                } else {
                    hideProgress(spinner)
                }


            }
        })
    }

    /**
     * populate api response
     */
    override fun showResponse() {
        viewModel.response.observe(this, Observer { model ->

            if (model.total_items > 0) {
                //adjusting bounds
                val latLngBounds = LatLngBounds.builder()
                mMap.clear()
                val eventList = model.events.event
                for (data in eventList) {
                    val latLng = LatLng(data.latitude, data.longitude)
                    latLngBounds.include(latLng)
                    mMap.addMarker(
                        MarkerOptions().position(latLng).title(data.title).icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                    )
                }

                val bounds = latLngBounds.build()
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        DEFAULT_MAP_ZOOM.toInt()
                    )
                )

            } else {
                showToast(getString(R.string.error_sorry_no_event_found))
            }

        })

    }

    override fun showFilterResponse() {
        viewModel.placeDetailresponse.observe(this, Observer { repos ->
            repos?.let {
                val latitude = repos.result.geometry.location.lat.toString()
                val longitude = repos.result.geometry.location.lng.toString()
                val loc = "$latitude,$longitude"
                val hashMap: HashMap<String, String> = HashMap()
                hashMap[PARAM_API_KEY] = getString(R.string.eventful_api_key)
                hashMap[KEY_WHERE_LOCATION] = loc
                hashMap[KEY_WHERE_LOCATION_WITH_IN] = RADIUS
                callEventsApi(hashMap)

            }
        })
    }

}
