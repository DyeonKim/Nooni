package com.ssafy.nooni.config

import android.app.Application
import com.google.gson.GsonBuilder
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass:Application() {
    val BASE_URL = "http://70.12.130.105:5000"
    val PRDINFO_SERVER_URL = "http://apis.data.go.kr/B553748/CertImgListService/"
    val TIME_OUT = 10000L

    companion object{
        lateinit var sRetrofit: Retrofit
        lateinit var pRetrofit: Retrofit
    }
    override fun onCreate() {
        super.onCreate()
        initRetrofit()
        KakaoSdk.init(this, "c09ab9ab21d2c70cd982b6dd34ff6126")
    }
    fun initRetrofit() {
        val gson = GsonBuilder().setLenient().create()

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

        pRetrofit = Retrofit.Builder().baseUrl(PRDINFO_SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}