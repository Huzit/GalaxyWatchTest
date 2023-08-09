package com.samsung.android.hardware.sensormanager

class SemSkinTemperatureSensorParam {
    //데이터 요청 모드 설정
    enum class Mode(i: Int) {
        TEST(0),
        ONDEMAND(1),
        CONTINUOUS(2),
        ALL(3);

        companion object{
            //현재 Enum 타입의 배열을 반환
            private var array: Array<Mode> = values()
            fun getArray() = array
        }
        private var value: Int = i
        fun getValue() = value
    }
    //정확성 설정
    enum class Accuracy(i: Int){
        ERROR(-1),
        NORMAL(0);

        companion object {
            private var array = values();
            fun getArray(): Array<Accuracy> = array
        }
        private var value: Int = i
        fun getValue() = value
    }
    //데이터 타입 설정
    enum class DataType(i: Int){
        NORMAL(0),
        FLUSH_END(1),
        DEBUG(2);

        companion object{
            private var array = values()
            fun getArray(): Array<DataType> = array
        }
        private var value: Int = i
        fun getValues() = value
    }
}