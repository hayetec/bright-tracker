package com.issenur.brighttracker.meal

data class StudentMealRecordUpdateRequest(
    val amSnackEaten: Boolean = false,
    val lunchEaten: Boolean = false,
    val pmSnackEaten: Boolean = false
)