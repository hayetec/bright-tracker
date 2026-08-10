package com.issenur.brighttracker.enrollment

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/classrooms/{classroomId}/students")
class StudentEnrollmentController(
    private val studentEnrollmentService: StudentEnrollmentService
) {

    @PostMapping("/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun enroll(
        @PathVariable classroomId: Long,
        @PathVariable studentId: Long
    ): StudentEnrollmentResponse =
        studentEnrollmentService.enroll(
            classroomId,
            studentId
        )

    @GetMapping
    fun findAll(
        @PathVariable classroomId: Long
    ): List<StudentEnrollmentResponse> =
        studentEnrollmentService.findByClassroom(classroomId)

    @DeleteMapping("/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable classroomId: Long,
        @PathVariable studentId: Long
    ) {
        studentEnrollmentService.remove(
            classroomId,
            studentId
        )
    }
}