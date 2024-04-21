package com.samsung.android.hardware.sensormanager

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable


/* loaded from: classes2.dex */
open class SemSensorAttribute() : Parcelable {
    private var mAttribute: Bundle? = Bundle()

    companion object CREATOR: Parcelable.Creator<SemSensorAttribute?>{
        const val FLUSH_WAKE_LOCK_FLAG = 1

        override fun createFromParcel(parcel: Parcel): SemSensorAttribute? {
            return SemSensorAttribute(parcel)
        }

        override fun newArray(i: Int): Array<SemSensorAttribute?> {
            return arrayOfNulls(i)
        }
    }

    override fun describeContents() = 0

    constructor(parcel: Parcel) : this() {
        readFromParcel(parcel)
    }

    fun getAttribute(i: Int): Bundle? {
        val num = i.toString()
        return if (mAttribute!!.containsKey(num)) {
            mAttribute!!.getBundle(num)
        } else null
    }

    fun setAttribute(i: Int, bundle: Bundle?) {
        mAttribute!!.putBundle(i.toString(), bundle)
    }

    protected fun setWakeLockAttribute(i: Int, bundle: Bundle?, i2: Int) {
        mAttribute!!.putInt("wake_lock_flag", i2)
        mAttribute!!.putBundle(i.toString(), bundle)
    }

    val wakeLockAttributeFlag: Int
        get() = mAttribute!!.getInt("wake_lock_flag", -1)
    val isWakeLockAttribute: Boolean
        get() = mAttribute!!.getInt("wake_lock_flag", -1) != -1

    // android.os.Parcelable
    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeBundle(mAttribute)
    }

    private fun readFromParcel(parcel: Parcel) {
        mAttribute = parcel.readBundle(javaClass.classLoader)
    }
}