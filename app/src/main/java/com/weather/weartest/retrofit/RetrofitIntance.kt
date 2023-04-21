package com.weather.weartest.retrofit

import retrofit2.converter.gson.GsonConverterFactory

class RetrofitIntance {
    private val baseUrl = "https://jsonplaceholder.typicode.com"

    fun testTongsin() = retrofit2
        .Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}