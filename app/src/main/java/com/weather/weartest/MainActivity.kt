package com.weather.weartest

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorDirectChannel
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.common.io.Files.append
import com.samsung.android.hardware.sensormanager.*
import com.weather.weartest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {

    private final val APP_TAG = "SimpleHealth"
    private lateinit var binding: ActivityMainBinding
//    private lateinit var ambientController: AmbientModeSupport.AmbientController
    private lateinit var sensorManager : SemSensorManager
    lateinit var heartRateSensor: Sensor //심박 센서
    lateinit var offBodySensor: Sensor   //착용감지센서
    lateinit var test: Sensor
    val hrCode = Sensor.TYPE_HEART_RATE
    lateinit var findLocation: FindLocation
    val sensorCode = 1

    private var lastValue: Float = 0.0f

    private val sensorListener = object: SemSensorListener{
        override fun onEventChanged(semSensorEvent: SemSensorEvent) {
            Log.d("events", semSensorEvent.toString())
            val semSkinTemperatureSensorEvent = SemSensorEvent() as SemSkinTemperatureSensorEvent
            var count = semSkinTemperatureSensorEvent.getCount()!!.minus(1) ?: 0

            if(count >= 0 && semSkinTemperatureSensorEvent.getAccuracyList()[count] != SemSkinTemperatureSensorParam.Accuracy.ERROR){
//                val num = sensorManager.sensorMap["TYPE_WEAR_SKIN_TEMPERATURE"]!!
                val num = 50
                sensorManager.unregisterListener(this, num)
            }

            lastValue = semSkinTemperatureSensorEvent.getObjectTemperatureList()!![count]

            Log.d("디폴트 값", lastValue.toString())

            window.clearFlags(128)
            val file = File(cacheDir, "sensor_log")
            val sb = StringBuilder("sv=")
            sb.append(
                if(sensorManager != null)
                    sensorManager.getServiceVersion()
                else
                    null
            ).append("&v=")
            .append(
            if(sensorManager != null)
                sensorManager.getVersion()
            else
                null
            ).append("&a=").append(
                semSkinTemperatureSensorEvent.getAccuracyList()[count]
            ).append("&at=").append(
                semSkinTemperatureSensorEvent.getAmbientTemperatureList()!![count]
            ).append("&ot=").append(
                semSkinTemperatureSensorEvent.getObjectTemperatureList()!![count]
            ).append('\n')


        }

        override fun onStatusChanged(i: Int, str: String) {
            Log.d("asdf", "i: $i , str: $str")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sensorManager = SemSensorManager(this)
        sensorManager.test("Tlqkf")

        binding.runTemp.setOnClickListener {
            measure()
        }

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

    private fun measure() {
//        window.addFlags(128)
        if(sensorManager != null){
//            val num = sensorManager.sensorMap["TYPE_WEAR_SKIN_TEMPERATURE"]
            val num = 50

            if(num != null){
                val res =sensorManager.registerListener(sensorListener, num.toInt()) //fixme <- -1이 반환왜
                Log.d("measure", res.toString())
//                if(sensorManager.registerListener(sensorListener, num.toInt()) == 0){
//                    loading()
//                    vibrate()
//                }
            }
        }
    }


    private fun calibrateCel(cel: Float): Float{
        return lastValue
    }
    
    override fun onBackPressed() {
    
    }
    
    var lastPress = 0L
    var pressCount = 0
    val doubleClickInterval = 400L
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        
        if(keyCode == 4){
            if(pressCount > 0 && (System.currentTimeMillis() - lastPress) < doubleClickInterval)
                pressCount += 1
            else
                pressCount = 1
            Log.d("키 테스트 이벤트", "pressCount : $pressCount")
            lastPress = System.currentTimeMillis()
            
            when(pressCount){
                2 -> {
                    Log.d("PressCount", "2회 클릭했음")
                }
                3 -> {
                    Log.d("PressCount", "3회 클릭했음")
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
    
    //센서 정의
//    fun initSensor(){
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//
//        heartRateSensor = sensorManager.getDefaultSensor(hrCode, true)
//
//        offBodySensor = sensorManager.getDefaultSensor(34)
//        test = sensorManager.getDefaultSensor(sensorCode)
//    }

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
//    override fun onSensorChanged(event: SensorEvent?) {
//
//        when(event!!.sensor.type){
//            hrCode -> {
//                val heartRate = event.values?.get(0) ?: 0
//                Log.d("센서 값 : ", "${heartRate}")
//                binding.hrTv.text = "심박수 : $heartRate "
//            }
//            34 -> {
//                val t = event.values?.get(0) ?: 0
//                Log.d("착용 감지 센서 : ", "$t")
//                binding.offBodyTv.text = "착용 감지 센서\n(0: 미착용, 1: 착용) :$t"
//            }
//            sensorCode -> {
//                val t = event.values[0] ?: 0
//
//                binding.test.text = "보로미터 값 : \n${event.sensor.name}\n$t"
//            }
//        }
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        Log.d("AccuracyChangedEvent", sensor!!.name)
//    }

    override fun onResume() {
        super.onResume()
//        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(this, offBodySensor, SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(this, test, SensorManager.SENSOR_DELAY_UI)

//        findLocation.startLocationUpdates()
    }
    override fun onDestroy() {
        super.onDestroy()
//        findLocation.stopLocationUpdates()
//        findLocation.getLocationDatabase()
    }
}