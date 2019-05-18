package com.habittracker.model

data class Child(val name: String? = "",
                 val date_of_birth: String? = "",
                 val reward: Int? = 0,
                 val totalrewards: Int? = 0,
                 val gender: String? = "",
                 val points: Int? = 0,
                 val coins: Int? = 0,
                 val level1: Int? = 0,
                 val level2: Int? = 0,
                 val level3: Int? = 0,
                 val level4: Int? = 0,
                 val level5: Int? = 0,
                 val habit: MutableMap<String, Habit>? = null)