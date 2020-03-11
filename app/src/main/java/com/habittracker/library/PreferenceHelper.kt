package com.habittracker.library

import android.content.Context

class PreferenceHelper(context: Context){
    companion object {
        const val USER_ID = "source.prefs.user.id"
        const val USER_NAME = "source.prefs.user.name"
    }

    private val sharedPreference = context.getSharedPreferences("data.source.prefs", Context.MODE_PRIVATE)
    private val editPreference = sharedPreference.edit()

    var userId: String?
        get() = sharedPreference.getString(USER_ID, "")
        set(value) = editPreference.putString(USER_ID, value).apply()

    var userName: String?
        get() = sharedPreference.getString(USER_NAME, "")
        set(value) = editPreference.putString(USER_NAME, value).apply()

    fun clearData(){
        editPreference.putString(USER_ID, "").apply()
        editPreference.putString(USER_NAME, "").apply()
    }
}