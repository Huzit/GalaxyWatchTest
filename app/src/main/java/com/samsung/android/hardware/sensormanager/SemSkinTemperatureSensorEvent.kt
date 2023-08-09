package com.samsung.android.hardware.sensormanager

import android.os.Bundle

class SemSkinTemperatureSensorEvent(): SemSensorEvent() {
    constructor(i: Int, bundle: Bundle): this(){
        mSensorId = i
        mContext = bundle
    }

    fun getMode(): SemSkinTemperatureSensorParam.Mode =  this.mContext!!.getSerializable("mode") as (SemSkinTemperatureSensorParam.Mode)
    fun getDataType(): SemSkinTemperatureSensorParam.DataType = this.mContext!!.getSerializable("data_type") as SemSkinTemperatureSensorParam.DataType
    fun getCount()                  = this.mContext?.getInt("count")
    fun getTimestampList()          = mContext!!.getLongArray("timestamp")
    fun getAmbientTemperatureList() = this.mContext!!.getFloatArray("amb_temp")
    fun getObjectTemperatureList()  = this.mContext!!.getFloatArray("obj_temp")
    fun getNCTemperatureList()      = mContext!!.getFloatArray("nc_temp")
    fun getAccuracyList() = mContext!!.getSerializable("accuracy") as Array<SemSkinTemperatureSensorParam.Accuracy>
    //작동 안할수도 있음
    fun getDebugData(): Array<ByteArray>? {
        val bArr = Array(60){ ByteArray(50) }
        val intArray = mContext!!.getIntArray("data")
        for (i in 0..9) {
            for (i2 in 0..13) {
                val i3 = i2 * 4
                val bArr2 = bArr[i]
                val i4 = intArray!![i * 14 + i2]
                bArr2[i3] = i4.toByte()
                bArr2[i3 + 1] = (i4 shr 8).toByte()
                bArr2[i3 + 2] = (i4 shr 16).toByte()
                if (i2 < 13) {
                    bArr2[i3 + 3] = (i4 shr 24).toByte()
                }
            }
        }
        return bArr
    }
}