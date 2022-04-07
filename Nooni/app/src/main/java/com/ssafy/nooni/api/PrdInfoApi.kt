package com.ssafy.nooni.api

import com.ssafy.nooni.BuildConfig
import com.ssafy.nooni.entity.PrdInfoResp
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PrdInfoApi {
    @GET("getCertImgListService")
    suspend fun getPrdInfo(
        @Query("prdlstReportNo") prdNo: String,
        @Query("serviceKey") key:String = BuildConfig.DATA_PORTAL_SERVICE_KEY,
        @Query("returnType") type: String = "json"
    ): Response<PrdInfoResp>
}
