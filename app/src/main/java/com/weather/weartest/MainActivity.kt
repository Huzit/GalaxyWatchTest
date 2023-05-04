package com.weather.weartest

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.weather.weartest.databinding.ActivityMainBinding
import com.weather.weartest.retrofit.EndPoint
import com.weather.weartest.retrofit.RetrofitIntance
import com.weather.weartest.retrofit.TongSinTestResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    lateinit var sensorManager : SensorManager
    lateinit var heartRateSensor: Sensor
    lateinit var offBodySensor: Sensor
    lateinit var test: Sensor
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    val sensorCode: Int = 69635

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted.
        }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //위치 권한
        locationPermissionRequest.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION))
        //권한 체크
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }else{
            //위치 설정
            createLocationRequest()
            //마지막 위치 찾기
            findLastLocation()
            //콜백 정의
            getLocationCallback()
            //업데이트 시작
            startLocationUpdates(locationRequest, locationCallback)
        }


        requestPermissions(arrayOf("android.permission.BODY_SENSORS"), 1)

        ambientController = AmbientModeSupport.attach(this)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        offBodySensor = sensorManager.getDefaultSensor(34)
        test = sensorManager.getDefaultSensor(sensorCode)
//        Log.d("센서 리스트", sensorManager.getSensorList(Sensor.TYPE_ALL).toString())
//        retrofitTest()

    }
    fun retrofitTest(){
        val service = RetrofitIntance().testTongsin().create(EndPoint::class.java)
        service.httpTest().enqueue(object : Callback<List<TongSinTestResponse>> {
            override fun onResponse(
                call: Call<List<TongSinTestResponse>>,
                response: Response<List<TongSinTestResponse>>
            ) {
                Log.d("통신 결과", response.body().toString() ?: "0")
                binding.callResultTv.text = "통신결과(바디 사이즈) : ${response.body()?.size}"
            }

            override fun onFailure(call: Call<List<TongSinTestResponse>>, t: Throwable) {
                Log.e("통신 결과", "$t")
                binding.callResultTv.text = "통신결과 : $t"
            }

        })
    }
    //GPS 센서 여부
    fun getGps() = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)

    // 지난 위치값 가져오기
    fun findLastLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location: Location? ->
            binding.test.text = "위치 정보 : ${location.toString()}"
            Log.d("location", "$location")
        }
    }
    //위치 설정
    fun createLocationRequest(){
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.apply {
            addOnSuccessListener { task ->
                Log.d("Success", "$task")
            }

            addOnFailureListener{ task ->
                Log.d("Faild", "$task")
            }
        }
    }

    fun startLocationUpdates(locationRequest: LocationRequest, locationCallback: LocationCallback){
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun getLocationCallback(){
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult ?: return
                locationResult.locations.forEach {
                    Log.d("locations", "$it")
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when(event!!.sensor.type){
            Sensor.TYPE_HEART_RATE -> {
                val heartRate = event.values?.get(0) ?: 0
                Log.d("센서 값 : ", "${heartRate}")
                binding.hrTv.text = "심박수 : $heartRate "
            }
            34 -> {
                val t = event.values?.get(0) ?: 0
                Log.d("착용 감지 센서 : ", "$t")
                binding.offBodyTv.text = "착용 감지 센서\n(0: 미착용, 1: 착용) :$t"
            }
            sensorCode -> {
                val t = event.values[0] ?: 0
                event.sensor.name
                Log.d("테스트 값 : ", "$t")
                binding.test.text = "테스트 값 : \n${event.sensor.name}\n$t"
            }
        }
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, offBodySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, test, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}

private class myAmbientCallback(): AmbientModeSupport.AmbientCallback(){
    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
    }

    override fun onAmbientOffloadInvalidated() {
        super.onAmbientOffloadInvalidated()
    }
}