package com.samsung.android.hardware.sensormanager

import android.content.Context
import android.os.*
import android.util.Log
import java.util.HashMap
import java.util.concurrent.CopyOnWriteArrayList


class SemSensorManager() {
    companion object{
        const val ERROR_ALREADY_REGISTERED = -2
        const val ERROR_NONE = 0
        const val ERROR_NOT_SUPPORTED = -3
        const val ERROR_PERMISSION_DENIED = -1
        const val SEM_SENSOR_MANAGER_VERSION = "0.0.85"
        private const val TAG = "SemSensorManager"
    }

    private var mPackageName: String? = null
    private val mListenerDelegates = CopyOnWriteArrayList<ListenerDelegate>()
    /**/
    private val mServiceManager = Class.forName("android.os.ServiceManager")
    private val serviceManager = mServiceManager.getMethod("getService", String.javaClass)
    private val getService = serviceManager.invoke(null, arrayOf("sem_sensor")) as IBinder
    /**/
    private val mSemSensorService = ISemSensorService.Stub.asInterface(getService)

    private lateinit var _samSensorMap: HashMap<String, Int>
    val sensorMap: HashMap<String, Int>
        get() = _samSensorMap

    @OptIn(ExperimentalStdlibApi::class)
    constructor(context: Context): this() {

        Log.d("클래스 이름", Class.forName("android.os.ServiceManager").name)
        mPackageName = context.packageName
        Log.i(TAG, "SemSensorManager created")
        val cls2 = SemSensor.values()
        val hashMap = hashMapOf<String, Int>()
        //사용가능한 센서 목록을 가져옴
        for (semSensor in cls2){
            if(isAvailable(semSensor.ordinal)){
                hashMap[semSensor.name] = semSensor.ordinal
            }
        }

        mPackageName += ".dont.block.me.please"

//        val declaredField = javaClass.getDeclaredField("mPackageName")
//        declaredField.isAccessible = true
//        declaredField.set(this, context.packageName + ".dont.block.me.please")
//        declaredField.isAccessible = false
    }

    /* loaded from: classes2.dex */
    inner class ListenerDelegate(val listener: SemSensorListener) : ISemSensorCallback.Stub() {
        private val mHandler: Handler

        init {
            mHandler = object : Handler(Looper.getMainLooper()) {
                // from class: com.samsung.android.hardware.sensormanager.SemSensorManager.ListenerDelegate.1
                // android.os.Handler
                override fun handleMessage(message: Message) {
                    val i11: Int = message.what
                    if (i11 != 0) {
                        if (i11 == 1) {
                            listener.onStatusChanged(
                                message.arg1,
                                (message.obj as String)
                            )
                            return
                        }
                        Log.e(TAG, "Unknown message type: " + message.what)
                        return
                    }
                    val sensorEvent = message.obj as SensorEvent
                    listener.onEventChanged(getEvent(sensorEvent)!!)
                    if (this@SemSensorManager.mSemSensorService == null) {
                        Log.e(
                            TAG,
                            "mSemSensorService null in notifyWakeLockEventPropagated - system service not found"
                        )
                        return
                    }
                    try {
                        if (sensorEvent.isWakeLockEvent()) {
                            this@SemSensorManager.mSemSensorService.notifyWakeLockEventPropagated(
                                sensorEvent.getWakeLockEventFlag(),
                                sensorEvent.getType(),
                                sensorEvent.getWakeLockTimestamp()
                            )
                        }
                    } catch (e11: RemoteException) {
                        Log.e(
                            TAG,
                            "RemoteException in notifyWakeLockEventPropagated: ",
                            e11
                        )
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        fun getEvent(sensorEvent: SensorEvent): SemSensorEvent? {
            return when (sensorEvent.getType()) {
//                1 -> SemPedometerSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                2 -> SemExerciseSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                3 -> SemExerciseCalorieSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                4 -> SemIndoorSwimmingSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                5 -> SemOutdoorSwimmingSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                6 -> SemAutoSwimmingSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                7 -> SemStairTrackerSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                8 -> SemActivityTrackerSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                9 -> SemStepLevelSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                10 -> SemAutoSessionSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                11 -> SemDynamicWorkoutSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                12 -> SemFallDetectionSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                13 -> SemRunningDynamicsSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                14, 15, 16, 20, 21, 23, 29, 34, 43, 45 -> {
//                    Log.e(
//                        SemSensorManager.TAG,
//                        "SemSensorManager Unknown sensor event type: " + sensorEvent.getType()
//                    )
//                    null
//                }
//
//                17 -> SemHandwashSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                18 -> SemAltitudeSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                19 -> SemSLocationSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                22 -> SemAFELoggingSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                24 -> SemHrSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                25 -> SemDailyHrSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                26 -> SemPpgSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                27 -> SemEcgSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                28 -> SemBpManualSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                30 -> SemSleepDetectorSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                31 -> SemSleepAnalyzerSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                32 -> SemStressSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                33 -> SemVo2SensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                35 -> SemSpo2ManualSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                36 -> SemSpo2ContinuousSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                37 -> SemPpgIhrnSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                38 -> SemBiaSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                39 -> SemSweatLossSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                40 -> SemHrRawSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                41 -> SemInactiveTimeSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                42 -> SemRepCountSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                44 -> SemRegCtrlSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                46 -> SemWheelChairSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                47 -> SemCycleMonitorSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                48 -> SemSleepDetectFeatureSensorEvent(
//                    sensorEvent.getType(),
//                    sensorEvent.getContext()
//                )
//
//                49 -> SemSleepWatchSpO2SensorEvent(sensorEvent.getType(), sensorEvent.getContext())
                50 -> SemSkinTemperatureSensorEvent(
                    sensorEvent.getType(),
                    sensorEvent.getContext()!!
                )

//                51 -> SemGpsSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
//                52 -> SemStatisticSensorEvent(sensorEvent.getType(), sensorEvent.getContext())
                else -> {
                    Log.e(
                        TAG,
                        "SemSensorManager Unknown sensor event type: " + sensorEvent.getType()
                    )
                    null
                }
            }
        }

        @Synchronized  // com.samsung.android.hardware.sensormanager.ISemSensorCallback
        override fun onEventChanged(sensorEvent: SensorEvent?) {
            val obtain: Message = Message.obtain()
            obtain.what = 0
            obtain.obj = sensorEvent
            mHandler.sendMessage(obtain)
        }

        @Synchronized  // com.samsung.android.hardware.sensormanager.ISemSensorCallback
        override fun onStatusChanged(i11: Int, str: String?) {
            val obtain: Message = Message.obtain()
            obtain.what = 1
            obtain.obj = str
            obtain.arg1 = i11
            mHandler.sendMessage(obtain)
        }
    }

    private fun getListenerDelegate(semSensorListener: SemSensorListener?): ListenerDelegate? {
        if (semSensorListener == null || mListenerDelegates.isEmpty()) {
            return null
        }
        val it2: Iterator<ListenerDelegate> = mListenerDelegates.iterator()
        while (it2.hasNext()) {
            val next = it2.next()
            if (next.listener == semSensorListener) {
                return next
            }
        }
        return null
    }

    fun getVersion(): String? {
        return SEM_SENSOR_MANAGER_VERSION
    }

//    fun getSensorMap(): HashMap<String, Int> {
//        return _sensorMap
//    }

    fun getSensorVersion(i11: Int): Int {
        val iSemSensorService = mSemSensorService
        if (iSemSensorService == null) {
            Log.e(TAG, "mSemSensorService null in isFeatureSupported - system service not found")
            return -1
        }
        return try {
            iSemSensorService.getSensorVersion(i11)
        } catch (e11: RemoteException) {
            Log.e(TAG, "RemoteException in isFeatureSupported: ", e11)
            -1
        }
    }

    fun getServiceVersion(): String? {
        if (mSemSensorService == null) {
            Log.e(TAG, "mSemSensorService null in getServiceVersion - system service not found")
            return null
        }
        return try {
            Log.i(TAG, "registerListener")
            mSemSensorService.getVersion()
        } catch (e11: RemoteException) {
            Log.e(TAG, "RemoteException in getServiceVersion: ", e11)
            null
        }
    }

    fun getSupportedFeatures(): Map<Int?, Map<String?, Boolean?>?>? {
        val iSemSensorService = mSemSensorService
        if (iSemSensorService == null) {
            Log.e(TAG, "mSemSensorService null in getSupportedFeatures - system service not found")
            return null
        }
        return try {
            iSemSensorService.getSupportedFeatures() as Map<Int?, Map<String?, Boolean?>?>
        } catch (e11: RemoteException) {
            Log.e(TAG, "RemoteException in getSupportedFeatures: ", e11)
            null
        }
    }

    fun isAvailable(i11: Int): Boolean {
        val iSemSensorService = mSemSensorService
        if (iSemSensorService == null) {
            Log.e(TAG, "mSemSensorService null in isAvailable - system service not found")
            return false
        }
        return try {
            iSemSensorService.isAvailable(i11, mPackageName)
        } catch (e11: RemoteException) {
            Log.e(TAG, "RemoteException in isAvailable: ", e11)
            false
        }
    }

    fun isFeatureSupported(i11: Int, str: String?): Boolean {
        val iSemSensorService = mSemSensorService
        if (iSemSensorService == null) {
            Log.e(TAG, "mSemSensorService null in isFeatureSupported - system service not found")
            return false
        }
        return try {
            iSemSensorService.isFeatureSupported(i11, str)
        } catch (e11: RemoteException) {
            Log.e(TAG, "RemoteException in isFeatureSupported: ", e11)
            false
        }
    }

    fun registerListener(semSensorListener: SemSensorListener, i11: Int): Int {
        return registerListener(semSensorListener, i11, null)
    }

    private fun registerListener(
        semSensorListener: SemSensorListener,
        i11: Int,
        semSensorAttribute: SemSensorAttribute?
    ): Int {
        if (mSemSensorService == null) {
            Log.e(TAG, "mSemSensorService null in registerListener - system service not found")
            return -3
        }
        var i12 = 0
        var listenerDelegate = getListenerDelegate(semSensorListener)
        if (listenerDelegate == null) {
            listenerDelegate = ListenerDelegate(semSensorListener)
            mListenerDelegates.add(listenerDelegate)
        }
        try {
            //여기 안타는데???
            Log.i(TAG, "registerListener")
            i12 = mSemSensorService.registerCallback(
                listenerDelegate, i11,
                mPackageName, semSensorAttribute
            )
            Log.i(TAG, "$i12")
        } catch (e11: RemoteException) {
            Log.e(TAG, "RemoteException in registerListener: ", e11)
        }
        Log.i(TAG, "registerListener : end")
        return i12
    }

    fun setAttribute(
        semSensorListener: SemSensorListener?,
        i11: Int,
        semSensorAttribute: SemSensorAttribute?
    ): Boolean {
        val str: String
        Log.i(TAG, "SemSensorManager setAttribute")
        str = if (mSemSensorService == null) {
            "mSemSensorService null in setAttribute - system service not found"
        } else {
            val listenerDelegate = getListenerDelegate(semSensorListener)
            if (listenerDelegate == null) {
                "SemSensorManager setAttribute listenerDelegate null"
            } else if (semSensorAttribute != null) {
                return try {
                    if (mSemSensorService.setAttribute(listenerDelegate, i11, semSensorAttribute)) {
                        Log.i(TAG, "setAttribute sensor type $i11")
                        return true
                    }
                    true
                } catch (e11: RemoteException) {
                    Log.e(TAG, "RemoteException in setAttribute", e11)
                    true
                }
            } else {
                "SemSensorManager setAttribute attribute null"
            }
        }
        Log.e(TAG, str)
        return false
    }

    fun unregisterListener(semSensorListener: SemSensorListener?, i11: Int) {
        if (mSemSensorService == null) {
            Log.e(TAG, "mSemSensorService null in unregisterListener - system service not found")
            return
        }
        val listenerDelegate = getListenerDelegate(semSensorListener) ?: return
        try {
            if (mSemSensorService.unregisterCallback(listenerDelegate, i11)) {
                mListenerDelegates.remove(listenerDelegate)
                Log.i(TAG, "unregisterListener")
            }
        } catch (e11: RemoteException) {
            Log.e(TAG, "RemoteException in unregisterListener: ", e11)
        }
    }
}