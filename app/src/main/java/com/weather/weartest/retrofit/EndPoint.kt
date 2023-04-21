package com.weather.weartest.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface EndPoint {
    @GET("/photos")
    fun httpTest(): Call<List<TongSinTestResponse>>
}