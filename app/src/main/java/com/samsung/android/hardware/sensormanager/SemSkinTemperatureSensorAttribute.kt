package com.samsung.android.hardware.sensormanager

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import java.util.EnumSet

class SemSkinTemperatureSensorAttribute(): SemSensorAttribute() {
    companion object Creator: Parcelable.Creator<SemSkinTemperatureSensorAttribute>{
        override fun createFromParcel(parcel: Parcel): SemSkinTemperatureSensorAttribute = SemSkinTemperatureSensorAttribute(parcel)

        override fun newArray(size: Int): Array<SemSkinTemperatureSensorAttribute?> = arrayOfNulls(size)
    }

    var mAttribute: Bundle?
    lateinit var mCmd: EnumSet<Cmd>

    //명령어 모음
    enum class Cmd{
        DEBUG,
        FLUSH,
        FREQUENCY,
        MODE
    }

    init {
        mAttribute = null
        mCmd = EnumSet.noneOf(Cmd::class.java)
    }

    constructor(parcel: Parcel): this(){
        SemSensorAttribute(parcel)
        mAttribute = null
        mCmd = EnumSet.noneOf(Cmd::class.java)
    }

    fun setMode(mode: SemSkinTemperatureSensorParam.Mode): Boolean{
        if(mAttribute == null)
            mAttribute = Bundle()

        mCmd.add(Cmd.MODE)
        mAttribute!!.putSerializable("cmd", mCmd)
        mAttribute!!.putSerializable("mode", mode)
        super.setAttribute(50, mAttribute)
        return true
    }
    //50 = SkinTemp Bundle Code(삼성 헬스에서)
    fun setDebugMode(): Boolean {
        if(mAttribute == null){
            mAttribute = Bundle()
        }
        mCmd.add(Cmd.DEBUG)
        mAttribute!!.putSerializable("cmd", mCmd)
        super.setAttribute(50, mAttribute)
        return true
    }

    fun setFlush(): Boolean{
        if(mAttribute == null)
            mAttribute = Bundle()
        mCmd.add(Cmd.FLUSH)
        mAttribute!!.putSerializable("cmd", mCmd)
        super.setAttribute(50, mAttribute)
        return true
    }
}