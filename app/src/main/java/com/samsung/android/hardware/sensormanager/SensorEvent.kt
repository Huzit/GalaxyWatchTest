package com.samsung.android.hardware.sensormanager

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable


class SensorEvent(): Parcelable {
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SensorEvent> = object : Parcelable.Creator<SensorEvent> {
            override fun createFromParcel(parcel: Parcel): SensorEvent? {
                return SensorEvent(parcel)
            }

            override fun newArray(i: Int): Array<SensorEvent?> {
                return arrayOfNulls(i)
            }
        }
    }
    var mContext: Bundle? = null
    var mSensorEvent: SensorEvent? = null
    var mSensorId: Int

    init {
        mSensorId = -1
    }

    constructor(parcel: Parcel): this(){
        readFromParcel(parcel)
    }

    // android.os.Parcelable
    override fun describeContents() = 0

    fun getContext() = mContext
    fun getType() = mSensorId
    fun getWakeLockEventFlag(): Int = mContext!!.getInt("wake_lock_flag", -1)
    fun getWakeLockTimestamp(): Long = mContext!!.getLong("wake_lock_ts", -1L)
    fun isWakeLockEvent(): Boolean = mContext!!.getInt("wake_lock_flag", -1) != -1

    fun readFromParcel(parcel: Parcel) {
        mSensorId = parcel.readInt()
        mSensorEvent =
            parcel.readParcelable<Parcelable>(SensorEvent::class.java.classLoader) as SensorEvent?
        mContext = parcel.readBundle(javaClass.classLoader)
    }

    fun setValues(i: Int, bundle: Bundle?) {
        mSensorId = i
        mContext = bundle
    }

    // android.os.Parcelable
    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(mSensorId)
        parcel.writeParcelable(mSensorEvent, i)
        parcel.writeBundle(mContext)
    }
}