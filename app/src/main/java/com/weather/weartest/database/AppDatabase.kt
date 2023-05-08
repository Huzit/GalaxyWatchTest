package com.weather.weartest.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocationInfo::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
}