package com.habittracker.model

data class Habit(val id: String? = "",
                 val name: String? = "",
                 val point_plus: Int? = 0,
                 val point_minus: Int? = 0)