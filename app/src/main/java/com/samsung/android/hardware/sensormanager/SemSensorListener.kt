package com.samsung.android.hardware.sensormanager

interface SemSensorListener {
    fun onEventChanged(semSensorEvent: SemSensorEvent)
    fun onStatusChanged(i: Int, str: String)
}