package com.samsung.android.hardware.sensormanager

import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import androidx.annotation.RequiresApi


interface ISemSensorService: IInterface {
    companion object{
        val DESCRIPTOR: String
            get() = "com.samsung.android.hardware.sensormanager.ISemSensorService"
    }

    class Default : ISemSensorService {
        // android.os.IInterface
        override fun asBinder(): IBinder? = null

        override fun getSensorVersion(i: Int): Int = 0

        override fun getSupportedFeatures(): Map<*, *>? = null

        override fun getVersion(): String? = null

        override fun isAvailable(i: Int, str: String?): Boolean = false

        override fun isFeatureSupported(i: Int, str: String?): Boolean = false

        override fun notifyWakeLockEventPropagated(i: Int, i2: Int, j: Long): Boolean = false

        override fun registerCallback(
            iBinder: IBinder?,
            i11: Int,
            str: String?,
            semSensorAttribute: SemSensorAttribute?
        ): Int = 0

        override fun setAttribute(
            iBinder: IBinder?,
            i11: Int,
            semSensorAttribute: SemSensorAttribute?
        ): Boolean = false

        override fun unregisterCallback(iBinder: IBinder?, i11: Int): Boolean = false
    }

    abstract class Stub : Binder(), ISemSensorService {
        class Proxy(private val mRemote: IBinder) : ISemSensorService {
            override fun asBinder(): IBinder {
                return mRemote
            }

            val interfaceDescriptor: String
                get() = DESCRIPTOR

            
            override fun getSensorVersion(i: Int): Int {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                return try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    obtain.writeInt(i)
                    mRemote.transact(6, obtain, obtain2, 0)
                    obtain2.readException()
                    obtain2.readInt()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
            }

            
            override fun getSupportedFeatures(): Map<*, *>? {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                return try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    mRemote.transact(9, obtain, obtain2, 0)
                    obtain2.readException()
                    obtain2.readHashMap(javaClass.classLoader)
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
            }

            
            override fun getVersion(): String? {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                return try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    mRemote.transact(5, obtain, obtain2, 0)
                    obtain2.readException()
                    obtain2.readString()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
            }

            
            override fun isAvailable(i: Int, str: String?): Boolean {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                return try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    obtain.writeInt(i)
                    obtain.writeString(str)
                    mRemote.transact(4, obtain, obtain2, 0)
                    obtain2.readException()
                    obtain2.readBoolean()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
            }

            
            override fun isFeatureSupported(i: Int, str: String?): Boolean {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                return try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    obtain.writeInt(i)
                    obtain.writeString(str)
                    mRemote.transact(8, obtain, obtain2, 0)
                    obtain2.readException()
                    obtain2.readBoolean()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
            }

            
            override fun notifyWakeLockEventPropagated(i: Int, i2: Int, j: Long): Boolean {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                return try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    obtain.writeInt(i)
                    obtain.writeInt(i2)
                    obtain.writeLong(j)
                    mRemote.transact(7, obtain, obtain2, 0)
                    obtain2.readException()
                    obtain2.readBoolean()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
            }

            
            override fun registerCallback(
                iBinder: IBinder?,
                i: Int,
                str: String?,
                semSensorAttribute: SemSensorAttribute?
            ): Int {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                return try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    obtain.writeStrongBinder(iBinder)
                    obtain.writeInt(i)
                    obtain.writeString(str)
                    obtain.writeTypedObject(semSensorAttribute, 0)
                    mRemote.transact(1, obtain, obtain2, 0)
                    obtain2.readException()
                    obtain2.readInt()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
            }

            
            override fun setAttribute(
                iBinder: IBinder?,
                i: Int,
                semSensorAttribute: SemSensorAttribute?
            ): Boolean {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                return try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    obtain.writeStrongBinder(iBinder)
                    obtain.writeInt(i)
                    obtain.writeTypedObject(semSensorAttribute, 0)
                    mRemote.transact(3, obtain, obtain2, 0)
                    obtain2.readException()
                    obtain2.readBoolean()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
            }

            
            override fun unregisterCallback(iBinder: IBinder?, i: Int): Boolean {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                return try {
                    obtain.writeInterfaceToken(DESCRIPTOR)
                    obtain.writeStrongBinder(iBinder)
                    obtain.writeInt(i)
                    mRemote.transact(2, obtain, obtain2, 0)
                    obtain2.readException()
                    obtain2.readBoolean()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
            }
        }

        init {
            attachInterface(this, DESCRIPTOR)
        }

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
            val registerCallback: Int
            val unregisterCallback: Boolean
            if (i in 1..16777215) {
                parcel.enforceInterface(DESCRIPTOR)
            }
            if (i == 1598968902) {
                parcel2!!.writeString(DESCRIPTOR)
                return true
            }
            when (i) {
                1 -> {
                    val readStrongBinder = parcel.readStrongBinder()
                    val readInt = parcel.readInt()
                    val readString = parcel.readString()
                    parcel.enforceNoDataAvail()
                    registerCallback = registerCallback(
                        readStrongBinder, readInt, readString,
                        parcel.readTypedObject(SemSensorAttribute)
                    )
                    parcel2!!.writeNoException()
                    parcel2.writeInt(registerCallback)
                }

                2 -> {
                    val readStrongBinder2 = parcel.readStrongBinder()
                    val readInt2 = parcel.readInt()
                    parcel.enforceNoDataAvail()
                    unregisterCallback = unregisterCallback(readStrongBinder2, readInt2)
                    parcel2!!.writeNoException()
                    parcel2.writeBoolean(unregisterCallback)
                }

                3 -> {
                    val readStrongBinder3 = parcel.readStrongBinder()
                    val readInt3 = parcel.readInt()
                    parcel.enforceNoDataAvail()
                    unregisterCallback = setAttribute(
                        readStrongBinder3, readInt3,
                        parcel.readTypedObject(SemSensorAttribute)
                    )
                    parcel2!!.writeNoException()
                    parcel2.writeBoolean(unregisterCallback)
                }

                4 -> {
                    val readInt4 = parcel.readInt()
                    val readString2 = parcel.readString()
                    parcel.enforceNoDataAvail()
                    unregisterCallback = isAvailable(readInt4, readString2)
                    parcel2!!.writeNoException()
                    parcel2.writeBoolean(unregisterCallback)
                }

                5 -> {
                    val version = getVersion()!!
                    parcel2!!.writeNoException()
                    parcel2.writeString(version)
                }

                6 -> {
                    val readInt5 = parcel.readInt()
                    parcel.enforceNoDataAvail()
                    registerCallback = getSensorVersion(readInt5)
                    parcel2!!.writeNoException()
                    parcel2.writeInt(registerCallback)
                }

                7 -> {
                    val readInt6 = parcel.readInt()
                    val readInt7 = parcel.readInt()
                    val readLong = parcel.readLong()
                    parcel.enforceNoDataAvail()
                    unregisterCallback = notifyWakeLockEventPropagated(readInt6, readInt7, readLong)
                    parcel2!!.writeNoException()
                    parcel2.writeBoolean(unregisterCallback)
                }

                8 -> {
                    val readInt8 = parcel.readInt()
                    val readString3 = parcel.readString()
                    parcel.enforceNoDataAvail()
                    unregisterCallback = isFeatureSupported(readInt8, readString3)
                    parcel2!!.writeNoException()
                    parcel2.writeBoolean(unregisterCallback)
                }

                9 -> {
                    val supportedFeatures = getSupportedFeatures()!!
                    parcel2!!.writeNoException()
                    parcel2.writeMap(supportedFeatures)
                }

                else -> return super.onTransact(i, parcel, parcel2, i2)
            }
            return true
        }

        companion object {
            const val TRANSACTION_getSensorVersion = 6
            const val TRANSACTION_getSupportedFeatures = 9
            const val TRANSACTION_getVersion = 5
            const val TRANSACTION_isAvailable = 4
            const val TRANSACTION_isFeatureSupported = 8
            const val TRANSACTION_notifyWakeLockEventPropagated = 7
            const val TRANSACTION_registerCallback = 1
            const val TRANSACTION_setAttribute = 3
            const val TRANSACTION_unregisterCallback = 2
            fun asInterface(iBinder: IBinder?): ISemSensorService? {
                if (iBinder == null) {
                    return null
                }
                val queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR)
                return if (queryLocalInterface == null || queryLocalInterface !is ISemSensorService) Proxy(
                    iBinder
                ) else queryLocalInterface
            }
        }
    }

    fun getSensorVersion(i11: Int): Int

    fun getSupportedFeatures(): Map<*, *>?

    fun getVersion(): String?

    fun isAvailable(i11: Int, str: String?): Boolean

    fun isFeatureSupported(i11: Int, str: String?): Boolean

    fun notifyWakeLockEventPropagated(i11: Int, i12: Int, j11: Long): Boolean

    fun registerCallback(
        iBinder: IBinder?,
        i11: Int,
        str: String?,
        semSensorAttribute: SemSensorAttribute?
    ): Int

    fun setAttribute(iBinder: IBinder?, i11: Int, semSensorAttribute: SemSensorAttribute?): Boolean

    fun unregisterCallback(iBinder: IBinder?, i11: Int): Boolean
}