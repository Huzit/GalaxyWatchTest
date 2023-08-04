package com.weather.weartest

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorDirectChannel
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.weather.weartest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
//    private lateinit var ambientController: AmbientModeSupport.AmbientController
    lateinit var sensorManager : SensorManager
    lateinit var heartRateSensor: Sensor //심박 센서
    lateinit var offBodySensor: Sensor   //착용감지센서
    lateinit var test: Sensor
    val hrCode = Sensor.TYPE_HEART_RATE
    lateinit var findLocation: FindLocation
    val sensorCode = 6

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
        /**/
        getSensorList()

        requestPermissions(arrayOf("android.permission.BODY_SENSORS", android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),1001)
        findLocation = FindLocation(this, binding)
        findLocation.initDatabase()
        initSensor()

        //위치 권한

//        ambientController = AmbientModeSupport.attach(this)

        //위치 설정
//        findLocation.createLocationRequest()
        //마지막 위치 찾기
//        findLocation.findLastLocation()
        //콜백 정의
        //위치 업데이트 요청
//        startForegroundService(Intent(this, FindService::class.java))
    }

    //센서 정의
    fun initSensor(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        heartRateSensor = sensorManager.getDefaultSensor(hrCode, true)

        offBodySensor = sensorManager.getDefaultSensor(34)
        test = sensorManager.getDefaultSensor(sensorCode)
    }

    fun getSensorList(){
        val mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensors = ArrayList<String>()

        mSensorManager.getSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE).forEach {
            sensors.add("${it.name} ${it.type} ${it.isDirectChannelTypeSupported(SensorDirectChannel.TYPE_HARDWARE_BUFFER)} ${it.isWakeUpSensor}")
        }

        sensors.forEach{ n -> Log.d("sensorList", n)}
    }

    //GPS 센서 여부
    fun getGps() = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)

    //통신 테스트
/*    fun retrofitTest(){
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
    }*/

    //센서 이벤트
    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("SensorChangedEvent", event!!.sensor.name)

        when(event!!.sensor.type){
            hrCode -> {
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

                binding.test.text = "보로미터 값 : \n${event.sensor.name}\n$t"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("AccuracyChangedEvent", sensor!!.name)
    }

    override fun onResume() {
        super.onResume()
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ||
//            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//        ){
//            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), 2)
//        }
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, offBodySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, test, SensorManager.SENSOR_DELAY_UI)

//        if(findLocation.requestingLocationUpdates){
            findLocation.startLocationUpdates()
//            requestingLocationUpdates = false
//        }
    }

    override fun onPause() {
        super.onPause()
//        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        findLocation.stopLocationUpdates()
        findLocation.getLocationDatabase()

    }
}