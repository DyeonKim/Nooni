package com.ssafy.nooni.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TTSApi {
    @GET("/test")
    suspend fun getTTS(@Query("msg") sentence:String):Response<String>
}