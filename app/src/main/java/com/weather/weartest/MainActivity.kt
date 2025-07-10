package com.weather.weartest

import android.Manifest
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
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Text
import androidx.wear.input.WearableButtons
import com.google.android.gms.wearable.WearableListenerService
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.Value
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
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity(), SensorEventListener {

//    private final val APP_TAG = "SimpleHealth"
//    private lateinit var binding: ActivityMainBinding

    lateinit var sensorManager: SensorManager
    lateinit var heartRateSensor: Sensor //심박 센서
    lateinit var skinTempSensor: HealthTracker
    lateinit var ecgSensor: HealthTracker
    lateinit var offBodySensor: Sensor   //착용감지센서

    var heartText by mutableStateOf("")
    var tempText by mutableStateOf("")
    var ecgMessage by mutableStateOf("Press button measure ecg")
    var ecgValue by mutableStateOf("-0")
    var count by mutableLongStateOf(0L)
    var buttonText by mutableStateOf("측정 시작")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //레거시
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        requestPermissions(arrayOf("android.permission.BODY_SENSORS", android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        initTracker()
        initSensor()

        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(ecgMessage)
                Text("AVG ECG : $avgEcg")
                Button(
                    onClick = {
                        startMeasurement()
                    }
                ){
                    Text(buttonText)
                }
            }
        }
    }

//    private var pressCount = 0
//    private var lastPress = 0L
//    private var pressInterval = 400L
//    private fun pressPPP(pressEvnet: () -> Unit) = CoroutineScope(Dispatchers.Main).launch {
//        delay(pressInterval)
//        pressEvnet()
//    }
//
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        Log.d("pressed", event.toString())
//
//        if(keyCode != KeyEvent.KEYCODE_BACK)
//            return false
//
//        if(pressCount > 0 && (System.currentTimeMillis() - lastPress) < 400){
//            pressCount += 1
//        } else{
//            pressCount = 1
//        }
//        lastPress = System.currentTimeMillis()
//
//        when(pressCount){
//            2 -> {
//                pressPPP {
//                    if(pressCount == 2)
//                        Toast.makeText(applicationContext, "${pressCount}회 눌렀음", Toast.LENGTH_SHORT).show()
//                }
//        }
//            3 -> {
//                pressPPP {
//                    if(pressCount == 3)
//                        Toast.makeText(applicationContext, "${pressCount}회 눌렀음", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        return super.onKeyDown(keyCode, event)
//    }
//
//
//    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            Log.d("롱 프레스", "롱 프레스")
//        }
//        return super.onKeyLongPress(keyCode, event)
//    }



    //구글 센서 정의
    fun initSensor(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE, true)!!
        offBodySensor = sensorManager.getDefaultSensor(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT)!!
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
//    fun getGps() = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)

    //센서 이벤트
    override fun onSensorChanged(event: SensorEvent?) {
        when(event!!.sensor.type){
            Sensor.TYPE_HEART_RATE -> {
                val heartRate = event.values?.get(0) ?: 0
                Log.d("센서 값 : ", "${heartRate}")
            }
            Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> {
                val t = event.values?.get(0) ?: 0
                Log.d("착용 감지 센서 : ", "$t")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("AccuracyChangedEvent", sensor!!.name)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, offBodySensor, SensorManager.SENSOR_DELAY_NORMAL)
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
                HealthTrackerType.ECG_ON_DEMAND,
                HealthTrackerType.SKIN_TEMPERATURE_ON_DEMAND
            )
            ecgSensor = mHealthTrackingService.getHealthTracker(HealthTrackerType.ECG_ON_DEMAND)
            skinTempSensor = mHealthTrackingService.getHealthTracker(HealthTrackerType.SKIN_TEMPERATURE_ON_DEMAND)
            ecgSensor.setEventListener(sensorEventListener("ECG"))
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
    final val NO_CONTACT = 5
    var leadOff = AtomicBoolean() //손목 부착여부
    var isMeasurementRunning = AtomicBoolean() //측정 시작 여부
    var avgEcg = 0f
    final val MEASUREMENT_DURATION = 30000L
    final val MEASUREMENT_TICK = 1000L

    //측정 중 안내문구 표기
    val countDownTimer = object: CountDownTimer(MEASUREMENT_DURATION, MEASUREMENT_TICK){
        //ECG 측정 상태(leadeOff)에 따라 메시지 변경
        override fun onTick(timeLeft: Long) {
            if(timeLeft > MEASUREMENT_DURATION - 2000)
                return
            if(isMeasurementRunning.get())
                if(leadOff.get())
                    ecgMessage = "워치를 손목에 맞게 착용하고\n 홈 버튼에 검지를 올려주세요"
                else
                    ecgMessage = "측정 중. 남은시간: ${timeLeft/1000}second"
        }

        override fun onFinish() {
            ecgSensor.unsetEventListener()
            isMeasurementRunning.set(false)
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if(leadOff.get())
                ecgMessage = "측정 실패, 워치를 손목에 맞게 착용하고\n 홈 버튼에 검지를 올려주세요"
            else
                ecgMessage = "측정 완료 ECG : $avgEcg"
        }
    }

    private fun startMeasurement(){
        if(ActivityCompat.checkSelfPermission(applicationContext, "android.permission.BODY_SENSORS") == PackageManager.PERMISSION_DENIED)
            requestPermissions(arrayOf(Manifest.permission.BODY_SENSORS), 0)
        if(!isMeasurementRunning.get()){
            buttonText = "STOP"
            isMeasurementRunning.set(true)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            ecgSensor.setEventListener(sensorEventListener("ECG"))
            CoroutineScope(Dispatchers.IO).launch {
                countDownTimer.start()
            }
        } else{
            if(::ecgSensor.isInitialized)
                ecgSensor.unsetEventListener()
            isMeasurementRunning.set(false)
            ecgMessage = "Press Start And Put your index finger on watch home button"
            buttonText = "START"
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
                    }
                    "ECG" -> {
                        val isLeadOff = dataPoints[0].getValue(ValueKey.EcgSet.LEAD_OFF)
                        leadOff.set(isLeadOff == NO_CONTACT)
                        if(dataPoints.size == 0)
                            return
                        var sum = 0f
                        for(data in dataPoints){
                            val curEcg = data.getValue(ValueKey.EcgSet.ECG_MV)
                            sum += curEcg
                        }
                        avgEcg = sum/dataPoints.size

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