package com.habittracker.library

import android.content.Context
import android.preference.PreferenceManager

class PreferenceHelper(private val context: Context){
    companion object {
        const val USER_ID = "source.prefs.user.id"
        const val USER_NAME = "source.prefs.user.name"
    }

    private val preference = PreferenceManager.getDefaultSharedPreferences(context)

    var userId = preference.getString(USER_ID, "")
        set(value) = preference.edit().putString(USER_ID, value).apply()

    var userName = preference.getString(USER_NAME, "")
        set(value) = preference.edit().putString(USER_NAME, value).apply()

    fun clearData(){
        preference.edit().putString(USER_ID, "").apply()
        preference.edit().putString(USER_NAME, "").apply()
    }
}