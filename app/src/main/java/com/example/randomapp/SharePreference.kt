package com.example.randomapp

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharePreference(private val context: Context) {
    @SuppressLint("CommitPrefEdits")
    fun putBoonleanValue(key: String?, value: Boolean) {
        val sharedPreferences = context.getSharedPreferences(MY_SHARE_PREFERENCE, 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String?): Boolean {
        val sharedPreferences = context.getSharedPreferences(MY_SHARE_PREFERENCE, 0)
        return sharedPreferences.getBoolean(key, false)
    }

    @SuppressLint("CommitPrefEdits")
    fun putSwitchStatement(key: String?, value: List<String?>?) {
        val sharedPreferences = context.getSharedPreferences(MY_DATA, 0)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(value)
        editor.putString(key, json)
        editor.apply()
    }

    fun getSwitchStatement(key: String?): MutableList<String> {
        val sharedPreferences = context.getSharedPreferences(MY_DATA, 0)
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<List<String?>?>() {}.type
        val gson = Gson()
        return gson.fromJson(json, type)
    }

    companion object {
        private const val MY_SHARE_PREFERENCE = "MY_SHARE_PREFERENCE"
        private const val MY_DATA = "MY_DATA"
    }
}