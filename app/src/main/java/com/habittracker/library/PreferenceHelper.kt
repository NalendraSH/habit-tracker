package com.habittracker.library

import android.content.Context
import android.preference.PreferenceManager

class PreferenceHelper(private val context: Context){
    companion object {
        const val USER_ID = "source.prefs.user.id"
    }

    private val preference = PreferenceManager.getDefaultSharedPreferences(context)

    var userId = preference.getString(USER_ID, "")
        set(value) = preference.edit().putString(USER_ID, value).apply()
}