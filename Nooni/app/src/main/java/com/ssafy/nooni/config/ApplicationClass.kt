package com.ssafy.nooni.config

import android.app.Application
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass:Application() {
    val BASE_URL = "http://70.12.130.105:5000"
    val TIME_OUT = 10000L

    companion object{
        lateinit var sRetrofit: Retrofit
    }
    override fun onCreate() {
        super.onCreate()
        initRetrofit()
    }
    fun initRetrofit() {
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 보여줍니다.
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS).setLevel(
                    HttpLoggingInterceptor.Level.BODY))
            .build()

        sRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}