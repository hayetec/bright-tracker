package com.issenur.brighttracker.guardian

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class StudentGuardianController(
    private val studentGuardianService: StudentGuardianService
) {

    @PostMapping(
        "/api/students/{studentId}/guardians/{guardianId}"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun link(
        @PathVariable studentId: Long,
        @PathVariable guardianId: Long,
        @RequestBody request: StudentGuardianRequest
    ): StudentGuardianResponse =
        studentGuardianService.link(
            studentId,
            guardianId,
            request
        )

    @GetMapping(
        "/api/students/{studentId}/guardians"
    )
    fun findGuardiansByStudent(
        @PathVariable studentId: Long
    ): List<StudentGuardianResponse> =
        studentGuardianService.findGuardiansByStudent(
            studentId
        )

    @GetMapping(
        "/api/guardians/{guardianId}/students"
    )
    fun findStudentsByGuardian(
        @PathVariable guardianId: Long
    ): List<StudentGuardianResponse> =
        studentGuardianService.findStudentsByGuardian(
            guardianId
        )

    @PutMapping(
        "/api/students/{studentId}/guardians/{guardianId}"
    )
    fun update(
        @PathVariable studentId: Long,
        @PathVariable guardianId: Long,
        @RequestBody request: StudentGuardianRequest
    ): StudentGuardianResponse =
        studentGuardianService.update(
            studentId,
            guardianId,
            request
        )

    @DeleteMapping(
        "/api/students/{studentId}/guardians/{guardianId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable studentId: Long,
        @PathVariable guardianId: Long
    ) {
        studentGuardianService.remove(
            studentId,
            guardianId
        )
    }
}