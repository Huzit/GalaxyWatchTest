package com.weather.weartest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.weather.weartest.database.AppDatabase
import com.weather.weartest.database.LocationInfo
import com.weather.weartest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class FindLocation(val cnx: Context, val binding: ActivityMainBinding) {
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(cnx)
    var requestingLocationUpdates = true
    lateinit var db: AppDatabase

    init {
        createLocationRequest()
        getLocationCallback()
    }

    //위치 설정 초기 설정
    fun createLocationRequest(){
        locationRequest = LocationRequest.create().apply {
            interval = 3000         //인터벌 설정
            fastestInterval = 2000  //인터벌 설정
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(cnx)
        val task = client.checkLocationSettings(builder.build())

        task.apply {
            addOnSuccessListener { task ->
                Log.d("성공", "$task")
            }

            addOnFailureListener{ task ->
                Log.d("실패", "$task")
            }
        }
    }

    // 지난 위치값 가져오기
    fun findLastLocation(){
        if (ActivityCompat.checkSelfPermission(
                cnx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                cnx,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
                location: Location? ->
            binding.test.text = "위치 정보 : ${location?.latitude} ${location?.longitude}"
        }
    }

    //콜백 정의
    fun getLocationCallback() {
        locationCallback = object : LocationCallback(){
            //결과 콜백
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                (locationResult ?: return).locations.forEach {
                    this@FindLocation.binding.test.text = "위치 정보 : ${it.latitude} ${it.longitude}"
                    Log.d("locations", "${it.latitude} ${it.longitude} $it")

                    insertDatabase(LocationInfo(latitude = it.latitude, longitude = it.longitude, recordTime = LocalDateTime.now().toString()))
                }
            }

            //위치 가용성 콜백
            override fun onLocationAvailability(availbility: LocationAvailability) {
                super.onLocationAvailability(availbility)
                var logText = if(availbility.isLocationAvailable) "위치 사용 가능" else "위치 사용 불가"

                Log.d("availbility", logText)
            }
        }
    }

    //위치 업데이트
    fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                cnx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                cnx,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopLocationUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun initDatabase() {
        db = Room.databaseBuilder(
            cnx,
            AppDatabase::class.java,
            "locationDatabase"
        ).build()
    }

    fun insertDatabase(locationInfo: LocationInfo){
        CoroutineScope(Dispatchers.IO).launch {
            db.locationDao().insertLocation(locationInfo)
            Log.d("Inserted", "yes")
        }
    }

    fun getLocationDatabase(){
        CoroutineScope(Dispatchers.IO).launch{
            val a = db.locationDao().getLocations()
            Log.d("DatabaseLog", a.toString())
        }
    }
}