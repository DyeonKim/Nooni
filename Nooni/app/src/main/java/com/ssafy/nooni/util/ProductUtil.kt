package com.ssafy.nooni.util

import android.content.Context
import android.util.Log
import com.ssafy.nooni.entity.Product
import org.json.JSONArray

class ProductUtil {
    private val productList = arrayListOf<Product>()

    fun init(context: Context) {
        // JSON 파일 열어서 String으로 취득
        val assetManager = context.resources.assets
        val inputStream = assetManager.open("data.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        // JSONArray로 파싱
        val jsonArray = JSONArray(jsonString)

        for (index in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(index)

            val id = jsonObject.getString("id")
            val name = jsonObject.getString("name")
            val bcode = jsonObject.getString("bcode")
            val prdNo = jsonObject.getString("prdNo")

            productList.add(Product(id, name, bcode, prdNo))
        }
    }

    fun getProductData(id: Int): Product {
        return productList[id]
    }
}