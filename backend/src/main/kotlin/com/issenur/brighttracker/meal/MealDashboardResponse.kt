package com.issenur.brighttracker.meal

import java.time.LocalDate

data class MealDashboardResponse(
    val date: LocalDate,
    val totalStudents: Int,
    val amSnack: MealProgressResponse,
    val lunch: MealProgressResponse,
    val pmSnack: MealProgressResponse,
    val students: List<MealDashboardStudentResponse>,
)

data class MealProgressResponse(
    val eaten: Int,
    val remaining: Int,
)

data class MealDashboardStudentResponse(
    val studentId: Long,
    val firstName: String,
    val lastName: String,
    val classroomId: Long?,
    val classroomName: String?,
    val hasAllergies: Boolean,
    val amSnackEaten: Boolean,
    val lunchEaten: Boolean,
    val pmSnackEaten: Boolean,
)