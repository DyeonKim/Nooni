package com.ssafy.nooni.util

import android.content.Context
import com.ssafy.nooni.R
import org.json.JSONArray
import org.json.JSONException

class SharedPrefArrayListUtil(context: Context) {
    private val prefs = context.getSharedPreferences(
        context.getString(R.string.shared_file), Context.MODE_PRIVATE
    )

    fun setStringArrayPref(key: String, values: ArrayList<String>) {
        val editor = prefs.edit()
        val a = JSONArray()
        for (i in 0 until values.size) {
            a.put(values[i])
        }
        if (values.isNotEmpty()) {
            editor.putString(key, a.toString())
        } else {
            editor.putString(key, null)
        }
        editor.apply()
    }

    fun setAllergies(values: ArrayList<String>) {
        setStringArrayPref("allergies", values)
    }

    fun getStringArrayPref(key: String): ArrayList<String>? {
        val json = prefs.getString(key, null)
        val urls = ArrayList<String>()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    val url = a.optString(i)
                    urls.add(url)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return urls
    }

    fun getAllergies(): ArrayList<String>? {
        return getStringArrayPref("allergies")
    }
}