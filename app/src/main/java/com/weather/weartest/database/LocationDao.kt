package com.weather.weartest.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import retrofit2.http.GET

@Dao
interface LocationDao {
    @Insert
    fun insertLocation(vararg locations: LocationInfo)

    @Query("SELECT * FROM locationinfo")
    fun getLocations(): List<LocationInfo>
}