package com.intigo.intigotest.services

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters


class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private lateinit var locationManager : LocationManager
    private var  hasGps: Boolean = false
    private var  hasNetwork: Boolean = false
    private  var locationGps : Location? = null
    private  var locationNetwork : Location? = null
    override fun doWork(): Result {

        getLocation()
        return Result.success()
    }


    private fun getLocation() {
        // val uid = Firebase.auth.currentUser?.uid
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F)
                { p0 ->
                    if (p0 != null) {
                        locationGps = p0
                        Log.d("lat lng",locationGps.toString())
                        Toast.makeText(applicationContext," gps :${locationGps?.latitude} ; ${locationGps?.longitude}",
                            Toast.LENGTH_LONG).show()


                        /* if (uid != null) {
                                                             Firebase.firestore.collection("Drivers").document(uid).update("Longitude",
                                                                     locationGps!!.longitude,"Latitude", locationGps!!.latitude)
                                                                     .addOnSuccessListener {
                                                                         Snackbar.make(takeabreak, "Location Data feeds start", Snackbar.LENGTH_SHORT).show()
                                                                     }
                                                                     .addOnFailureListener {
                                                                         Snackbar.make(takeabreak, "Failed location feed", Snackbar.LENGTH_SHORT).show()
                                                                     }
                                                         }*/
                    }
                }

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }else if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F
                ) { p0 ->
                    if (p0 != null) {
                        locationNetwork = p0
                        Toast.makeText(applicationContext," network :${locationNetwork?.latitude} ; ${locationNetwork?.longitude}",
                            Toast.LENGTH_LONG).show()
                        /* if (uid != null) {
                                                             Firebase.firestore.collection("Drivers").document(uid).update("Longitude",
                                                                     locationNetwork!!.longitude,"Latitude", locationNetwork!!.latitude)
                                                                     .addOnSuccessListener {
                                                                         Snackbar.make(takeabreak, "Location Data feeds start", Snackbar.LENGTH_SHORT).show()
                                                                     }
                                                                     .addOnFailureListener {
                                                                         Snackbar.make(takeabreak, "Failed location feed", Snackbar.LENGTH_SHORT).show()
                                                                     }
                                                         }

                                                         */
                    }
                }

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }


        } else {
           applicationContext.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

}