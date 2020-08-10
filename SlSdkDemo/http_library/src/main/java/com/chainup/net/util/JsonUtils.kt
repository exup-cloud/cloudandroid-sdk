package com.chainup.net.util


import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.util.*

object JsonUtils {
    lateinit var gson: Gson

    fun <T> jsonToList(data: String, tClass: Class<T>): List<T> {
        val mList = ArrayList<T>()
        if (TextUtils.isEmpty(data)) return mList
        try {
            val mArray = JSONArray(data)
            (0 until mArray.length()).mapTo(mList) { jsonToBean(mArray.get(it).toString(), tClass) }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return mList
    }

    fun <T> jsonToBean(data: String, tClass: Class<T>): T = gson.fromJson(data, tClass)


    init {
        gson = Gson()
    }



}
