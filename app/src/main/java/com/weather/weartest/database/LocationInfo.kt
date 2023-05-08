package com.weather.weartest.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locationinfo")
data class LocationInfo(
    @PrimaryKey @ColumnInfo(name = "record_time")val recordTime: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
) {
}