package com.issenur.brighttracker.assignment

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/classrooms/{classroomId}/staff")
class ClassroomStaffAssignmentController(
    private val assignmentService: ClassroomStaffAssignmentService
) {

    @PostMapping("/{staffId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun assign(
        @PathVariable classroomId: Long,
        @PathVariable staffId: Long
    ): ClassroomStaffAssignmentResponse =
        assignmentService.assign(
            classroomId,
            staffId
        )

    @GetMapping
    fun findAll(
        @PathVariable classroomId: Long
    ): List<ClassroomStaffAssignmentResponse> =
        assignmentService.findByClassroom(
            classroomId
        )

    @DeleteMapping("/{staffId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable classroomId: Long,
        @PathVariable staffId: Long
    ) {
        assignmentService.remove(
            classroomId,
            staffId
        )
    }
}