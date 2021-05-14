package com.intigo.intigoassement.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.intigo.intigoassement.R
import com.intigo.intigoassement.helpers.LocationHelper
import com.intigo.intigoassement.helpers.MyLocationListener
import java.util.*

class LocationService : Service() {

    private val NOTIFICATION_CHANNEL_ID = "my_notification_location"
    private val TAG = "LocationService"
    var sharedPreferences: SharedPreferences? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
        sharedPreferences = this.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var calendar = Calendar.getInstance().timeInMillis

        val editor: SharedPreferences.Editor = sharedPreferences?.edit()!!
        editor.putLong("time", calendar)
        editor.apply()
        editor.commit()

        Log.d(TAG, "TIME IS ${calendar}")
        var timePssed = Calendar.getInstance().timeInMillis - sharedPreferences!!.getLong("time", 0)


        val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var mLocationListener = object : MyLocationListener {
            override fun onLocationChanged(location: Location?) {
                mLocation = location
                mLocation?.let {

                    Toast.makeText(
                        this@LocationService,
                        "onLocationChanged: Latitude ${it.latitude} , Longitude ${it.longitude}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(
                        TAG,
                        "onLocationChanged: Latitude ${it.latitude} , Longitude ${it.longitude}"
                    )
                }
                var timePssed =
                    Calendar.getInstance().timeInMillis - sharedPreferences!!.getLong("time", 0)
                if (timePssed > 180000) {
                    stopSelf()
                }
            }
        }
        LocationHelper().startListeningUserLocation(
            mLocationManager, this, mLocationListener,
            sharedPreferences!!
        )
        return START_STICKY

    }


    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false

    }

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }


}