package com.samsung.android.hardware.sensormanager

import android.os.Bundle

open class SemSensorEvent() {
    var mContext: Bundle? = null
    var mSensorId = 0

    constructor(i: Int, bundle: Bundle?): this() {
        mSensorId = i
        mContext = bundle
    }

    fun getType(): Int {
        return mSensorId
    }
}