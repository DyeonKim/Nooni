package com.ssafy.nooni.repository

import com.ssafy.nooni.api.TTSApi
import com.ssafy.nooni.config.ApplicationClass
import java.lang.Exception

class TTSRepository {
    val ttsApi = ApplicationClass.sRetrofit.create(TTSApi::class.java)

    // TODO: 0315 suhyeong : error 분기처리는 나중에.. flask 서버에서 그냥 스트링으로 url만넘겨줌
    suspend fun getWAVURL(sentence:String):String{
        return try {
            val response = ttsApi.getTTS(sentence)
            if(response.isSuccessful){
                return if(response.code()==200){
                    response.body()!!
                }else{
                    "error"
                }
            }else{
                "error"
            }
        }catch (e:Exception){
            "error"
        }
    }
}