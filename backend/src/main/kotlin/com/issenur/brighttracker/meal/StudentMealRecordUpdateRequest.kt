package com.issenur.brighttracker.meal

data class StudentMealRecordUpdateRequest(
    val breakfastEaten: Boolean = false,
    val lunchEaten: Boolean = false,
    val dinnerEaten: Boolean = false
)