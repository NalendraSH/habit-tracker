package com.habittracker.model

data class Timer(val mTimeLeftInMillis: Long,
                 val mTimerRunning: Boolean,
                 val mEndTime: Long)