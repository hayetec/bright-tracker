package com.issenur.brighttracker.meal

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/students/{studentId}/meals")
class StudentMealRecordController(
    private val mealRecordService: StudentMealRecordService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable studentId: Long,
        @RequestBody request: StudentMealRecordCreateRequest
    ): StudentMealRecordResponse =
        mealRecordService.create(
            studentId,
            request
        )

    @GetMapping
    fun findAll(
        @PathVariable studentId: Long
    ): List<StudentMealRecordResponse> =
        mealRecordService.findAllByStudent(
            studentId
        )

    @GetMapping("/{recordDate}")
    fun findByDate(
        @PathVariable studentId: Long,
        @PathVariable recordDate: LocalDate
    ): StudentMealRecordResponse =
        mealRecordService.findByStudentAndDate(
            studentId,
            recordDate
        )

    @PutMapping("/{recordDate}")
    fun update(
        @PathVariable studentId: Long,
        @PathVariable recordDate: LocalDate,
        @RequestBody request: StudentMealRecordUpdateRequest
    ): StudentMealRecordResponse =
        mealRecordService.update(
            studentId,
            recordDate,
            request
        )

    @DeleteMapping("/{recordDate}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable studentId: Long,
        @PathVariable recordDate: LocalDate
    ) {
        mealRecordService.delete(
            studentId,
            recordDate
        )
    }
}