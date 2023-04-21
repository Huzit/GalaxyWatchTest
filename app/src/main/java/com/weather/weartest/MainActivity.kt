package com.weather.weartest

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.ambient.AmbientModeSupport
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
    val sensorCode: Int = 69635


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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