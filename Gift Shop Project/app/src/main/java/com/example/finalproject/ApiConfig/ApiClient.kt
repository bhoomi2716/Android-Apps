package com.example.finalproject.ApiConfig

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient
{
    //Initialize Retrofit Library
    lateinit var retrofit : Retrofit
    var BaseUrl = "https://prakrutitech.buzz/AndroidAPI/"

    //Connection Established
    fun Connection() : Retrofit
    {
        retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }
}