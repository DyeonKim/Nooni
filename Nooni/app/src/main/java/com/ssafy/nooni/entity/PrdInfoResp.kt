package com.ssafy.nooni.entity

data class PrdInfoResp(
    val list: List<PrdInfo>,
    val resultCode: String,
    val resultMessage: String,
    val totalCount: String
)