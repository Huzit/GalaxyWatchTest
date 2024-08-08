package com.weather.weartest

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorDirectChannel
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaDrm
import android.media.UnsupportedSchemeException
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.KeyEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.input.WearableButtons
import com.google.android.gms.wearable.WearableListenerService
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import com.weather.weartest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.util.Base64
import java.util.UUID


class MainActivity : AppCompatActivity(), SensorEventListener {

    private final val APP_TAG = "SimpleHealth"
    private lateinit var binding: ActivityMainBinding
    lateinit var sensorManager: SensorManager
    lateinit var heartRateSensor: Sensor //심박 센서
    lateinit var skinTempSensor: HealthTracker
    lateinit var offBodySensor: Sensor   //착용감지센서
    lateinit var test: Sensor
    val hrCode = Sensor.TYPE_HEART_RATE
    lateinit var findLocation: FindLocation
    val sensorCode = 1

    private var lastValue: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ssaid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("ssaid", ssaid)
        binding.hrTv.text = ssaid
//        initTracker()
//        requestPermissions(arrayOf("android.permission.BODY_SENSORS", android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
//        requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),1001)
//        findLocation = FindLocation(this, binding)
//        findLocation.initDatabase()
//        initSensor()

        //위치 권한
//        ambientController = AmbientModeSupport.attach(this)
        //위치 설정
//        findLocation.createLocationRequest()
        //마지막 위치 찾기
//        findLocation.findLastLocation()

    }

    override fun onBackPressed() {

    }

    private var pressCount = 0
    private var lastPress = 0L
    private var pressInterval = 400L
    private fun pressPPP(pressEvnet: () -> Unit) = CoroutineScope(Dispatchers.Main).launch {
        delay(pressInterval)
        pressEvnet()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d("pressed", event.toString())

        if(keyCode != KeyEvent.KEYCODE_BACK)
            return false

        if(pressCount > 0 && (System.currentTimeMillis() - lastPress) < 400){
            pressCount += 1
        } else{
            pressCount = 1
        }
        lastPress = System.currentTimeMillis()
        binding.p3.text = pressCount.toString()

        when(pressCount){
            2 -> {
                pressPPP {
                    if(pressCount == 2)
                        Toast.makeText(applicationContext, "${pressCount}회 눌렀음", Toast.LENGTH_SHORT).show()
                }
        }
            3 -> {
                pressPPP {
                    if(pressCount == 3)
                        Toast.makeText(applicationContext, "${pressCount}회 눌렀음", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }


    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.d("롱 프레스", "롱 프레스")
        }
        return super.onKeyLongPress(keyCode, event)
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

    //센서 이벤트
    override fun onSensorChanged(event: SensorEvent?) {

        when(event!!.sensor.type){
            hrCode -> {
                val heartRate = event.values?.get(0) ?: 0
                Log.d("센서 값 : ", "${heartRate}")
                binding.hrTv.text = "심박수 : $heartRate "
            }
            34 -> {
                val t = event.values?.get(0) ?: 0
                Log.d("착용 감지 센서 : ", "$t")
            }
            sensorCode -> {
                val t = event.values[0] ?: 0
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("AccuracyChangedEvent", sensor!!.name)
    }

    override fun onResume() {
        super.onResume()
//        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(this, offBodySensor, SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(this, test, SensorManager.SENSOR_DELAY_UI)
//
//        findLocation.startLocationUpdates()
    }

    lateinit var mHealthTrackingService: HealthTrackingService

    fun initTracker(){
        mHealthTrackingService = HealthTrackingService(samsungConnectionListener, applicationContext)
        mHealthTrackingService.connectService()
    }

    private val samsungConnectionListener = object : ConnectionListener {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onConnectionSuccess() {
            val trackerList = listOf(
                HealthTrackerType.ACCELEROMETER,
                HealthTrackerType.HEART_RATE,
//                HealthTrackerType.SPO2,
                HealthTrackerType.SKIN_TEMPERATURE
            )
            skinTempSensor = mHealthTrackingService.getHealthTracker(HealthTrackerType.SKIN_TEMPERATURE)
            skinTempSensor.setEventListener(sensorEventListener("SKIN_TEMP"))
        }

        override fun onConnectionEnded() {
            Log.d("onConnectionEnded", "Connection is Ended")
        }

        override fun onConnectionFailed(e: HealthTrackerException?) {
            if (e != null) {
                if (e.errorCode == HealthTrackerException.OLD_PLATFORM_VERSION || e.errorCode == HealthTrackerException.PACKAGE_NOT_INSTALLED) {
                    Toast.makeText(
                        applicationContext,
                        "Health Platform version is outdated or not installed",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e(
                        "onConnectionFailed",
                        "Health Platform version is outdated or not installed"
                    )
                }

                if (e.hasResolution()) {
                }
            }
        }
    }

    private fun sensorEventListener(sensorName: String): HealthTracker.TrackerEventListener =
        object : HealthTracker.TrackerEventListener {
            override fun onDataReceived(dataPoints: MutableList<DataPoint>) {
                when (sensorName) {
                    "SKIN_TEMP" -> {
                        //AMBIENT_TEMPERATURE(주변온도) OBJECT_TEMPERATURE(물체온도?)
                        Log.d("SKIN TEMP", dataPoints[0].getValue(ValueKey.SkinTemperatureSet.OBJECT_TEMPERATURE).toString())
                        val skinTemp = dataPoints[0].getValue(ValueKey.SkinTemperatureSet.OBJECT_TEMPERATURE)
                        val ambientTemp = dataPoints[0].getValue(ValueKey.SkinTemperatureSet.AMBIENT_TEMPERATURE)
                        runOnUiThread {
                            binding.skinTemp.text = skinTemp.toString()
                            binding.skinTemp2.text = ambientTemp.toString()
                        }
                    }
                }
            }

            override fun onFlushCompleted() {
            }

            //TODO need Test
            override fun onError(e: HealthTracker.TrackerError?) {
                if (e == HealthTracker.TrackerError.PERMISSION_ERROR)
                    Log.e("PermissionError", "Permission Checked Failed")
                else if (e == HealthTracker.TrackerError.SDK_POLICY_ERROR)
                    Log.e("SDKPolicyError", "SDK Policy denied")
                else
                    Log.w("Error 발생", "onError called")
            }
        }
}