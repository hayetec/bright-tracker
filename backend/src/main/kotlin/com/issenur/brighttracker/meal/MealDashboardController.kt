package com.issenur.brighttracker.meal

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/meal-dashboard")
class MealDashboardController(
    private val mealDashboardService: MealDashboardService,
) {

    @GetMapping
    fun getDashboard(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        date: LocalDate
    ): MealDashboardResponse =
        mealDashboardService.getDashboard(date)
}