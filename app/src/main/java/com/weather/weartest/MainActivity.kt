package com.weather.weartest

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.weather.weartest.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding

    lateinit var sensorManager : SensorManager
    lateinit var heartRateSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions(arrayOf("android.permission.BODY_SENSORS"), 1)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.type == Sensor.TYPE_HEART_RATE){
            val heartRate = event.values?.get(0) ?: 0
            Log.d("센서 값 : ", "${heartRate}")
            binding.config.text = heartRate.toString()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}