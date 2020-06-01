package com.android.eventapplication

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.eventapplication.util.ConnectivityReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity(),
    ConnectivityReceiver.ConnectivityReceiverListener {
    abstract fun showError()
    abstract fun showPrgressbar()
    abstract fun showResponse()
    abstract fun setUpUIi()
    abstract fun showFilterResponse()
    private var isConnected: Boolean = false
    private var reciever: BroadcastReceiver? = null
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val REQUEST_ACCESS_LOCATION = 10
        private const val INTENT_FILTER = "android.net.conn.CONNECTIVITY_CHANGE"
        const val DEFAULT_MAP_ZOOM = 15f


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reciever = ConnectivityReceiver()
        if (reciever != null) {
            registerReceiver(
                reciever,
                IntentFilter(INTENT_FILTER)
            )
        }
        // Construct a FusedLocationProviderClient to get current location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_ACCESS_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpUIi()
            }
        }
    }

    fun checkIfPermissionGranted(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val grantedStatus = checkCallingOrSelfPermission(permission)
        return (grantedStatus == PackageManager.PERMISSION_GRANTED)
    }

    fun isConnctedToNetwork(): Boolean {
        return isConnected
    }

    fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }


    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
        if (reciever != null) {
            registerReceiver(
                reciever,
                IntentFilter(INTENT_FILTER)
            )
        }

    }

    override fun onPause() {
        super.onPause()
        ConnectivityReceiver.connectivityReceiverListener = null
        if (reciever != null) {
            unregisterReceiver(reciever)
        }
    }


    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        this.isConnected = isConnected
        if (!isConnected) {
            window.decorView.rootView.snack(
                getString(R.string.error_code_offline),
                Snackbar.LENGTH_LONG
            )
        }

    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, message, duration).show()
    }

    fun showProgress(progressbar: View) {
        progressbar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

    }

    fun hideProgress(progressbar: View) {
        progressbar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}