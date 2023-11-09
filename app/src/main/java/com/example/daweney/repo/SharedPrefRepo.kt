package com.example.daweney.repo

import android.content.Context

class SharedPrefRepo(val context:Context) {
    companion object{
        const val PROFILE="myPrefs"
        const val USERNAME="username"
        const val EMPTY=""
        const val CUSTOMER_ID="customerId"
        const val THEME="theme"
        const val LANGUAGE="language"
        const val PHONE="phone"
        const val ADDRESS="address"
    }

    fun saveData(fileName:String,key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getData(fileName:String,key: String, defaultValue: String): String? {
        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultValue)
    }


    fun deleteData(fileName:String,key: String){
        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(key).apply()
    }
}