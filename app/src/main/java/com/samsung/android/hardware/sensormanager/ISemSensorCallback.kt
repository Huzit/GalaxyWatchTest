package com.samsung.android.hardware.sensormanager

import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import androidx.annotation.RequiresApi


interface ISemSensorCallback: IInterface {
    companion object {
        val DESCRIPTOR: String
            get() = "com.samsung.android.hardware.sensormanager.ISemSensorCallback"

    }
    class Default : ISemSensorCallback {
        // android.os.IInterface
        override fun asBinder(): IBinder? = null

        // com.samsung.android.hardware.sensormanager.ISemSensorCallback
        override fun onEventChanged(sensorEvent: SensorEvent?) {}

        // com.samsung.android.hardware.sensormanager.ISemSensorCallback
        override fun onStatusChanged(i: Int, str: String?) {}

    }

    abstract class Stub : Binder(), ISemSensorCallback {
        companion object {
            const val TRANSACTION_onEventChanged = 1
            const val TRANSACTION_onStatusChanged = 2
        }

        class Proxy(private val mRemote: IBinder) : ISemSensorCallback {
            override fun asBinder(): IBinder {
                return mRemote
            }

            val interfaceDescriptor: String
                get() = DESCRIPTOR

            // com.samsung.android.hardware.sensormanager.ISemSensorCallback
            override fun onEventChanged(sensorEvent: SensorEvent?) {
                val obtain = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    obtain.writeTypedObject(sensorEvent, 0)
                    mRemote.transact(1, obtain, null, 1)
                } finally {
                    obtain.recycle()
                }
            }

            // com.samsung.android.hardware.sensormanager.ISemSensorCallback
            override fun onStatusChanged(i: Int, str: String?) {
                val obtain = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    obtain.writeInt(i)
                    obtain.writeString(str)
                    mRemote.transact(2, obtain, null, 1)
                } finally {
                    obtain.recycle()
                }
            }
        }

        init {
            attachInterface(this, DESCRIPTOR)
        }

        fun asInterface(iBinder: IBinder?): ISemSensorCallback? {
            if (iBinder == null) {
                return null
            }
            val queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR)
            return if (queryLocalInterface == null || queryLocalInterface !is ISemSensorCallback) Proxy(
                iBinder
            ) else queryLocalInterface
        }

        // android.os.IInterface
        override fun asBinder(): IBinder {
            return this
        }

        // android.os.Binder
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        public override fun onTransact(
            i: Int,
            parcel: Parcel,
            parcel2: Parcel?,
            i2: Int
        ): Boolean {
            if (i in 1..16777215) {
                parcel.enforceInterface(DESCRIPTOR)
            }
            if (i == 1598968902) {
                parcel2!!.writeString(DESCRIPTOR)
                return true
            }
            if (i == 1) {
                val sensorEvent =
                    parcel.readTypedObject(SensorEvent.CREATOR)
                parcel.enforceNoDataAvail()
                onEventChanged(sensorEvent)
            } else if (i != 2) {
                return super.onTransact(i, parcel, parcel2, i2)
            } else {
                val readInt = parcel.readInt()
                val readString = parcel.readString()
                parcel.enforceNoDataAvail()
                onStatusChanged(readInt, readString)
            }
            return true
        }
    }

    fun onEventChanged(sensorEvent: SensorEvent?)

    fun onStatusChanged(i11: Int, str: String?)
}