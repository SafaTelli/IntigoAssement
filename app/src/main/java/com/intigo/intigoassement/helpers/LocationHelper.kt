package com.intigo.intigoassement.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import java.util.*

class LocationHelper {
    var LOCATION_REFRESH_TIME = 10000 // 3 seconds. The Minimum Time to get location update
    var LOCATION_REFRESH_DISTANCE =
        0 // 0 meters. The Minimum Distance to be changed to get location update
    var locationListener: LocationListener? = null
    var TIME_SERVICE = 600000

    @SuppressLint("MissingPermission")
    fun startListeningUserLocation(
        locationManager: LocationManager,
        context: Context,
        myListener: MyLocationListener,
        sharedPreferences: SharedPreferences
    ) {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                myListener.onLocationChanged(location)
                var timePssed =
                    Calendar.getInstance().timeInMillis - sharedPreferences!!.getLong("time", 0)
                if (timePssed > TIME_SERVICE) {
                    locationManager.removeUpdates(this)
                }


            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        }


        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_REFRESH_TIME.toLong(),
            LOCATION_REFRESH_DISTANCE.toFloat(),
            locationListener as LocationListener
        )


    }

}

interface MyLocationListener {
    fun onLocationChanged(location: Location?)
}