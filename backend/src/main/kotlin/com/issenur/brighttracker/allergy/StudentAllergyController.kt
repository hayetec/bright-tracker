package com.issenur.brighttracker.allergy

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/students/{studentId}/allergies")
class StudentAllergyController(
    private val allergyService: StudentAllergyService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable studentId: Long,
        @Valid @RequestBody request: StudentAllergyCreateRequest
    ): StudentAllergyResponse =
        allergyService.create(
            studentId,
            request
        )

    @GetMapping
    fun findAll(
        @PathVariable studentId: Long
    ): List<StudentAllergyResponse> =
        allergyService.findAllByStudent(studentId)

    @GetMapping("/{allergyId}")
    fun findById(
        @PathVariable studentId: Long,
        @PathVariable allergyId: Long
    ): StudentAllergyResponse =
        allergyService.findById(
            studentId,
            allergyId
        )

    @PutMapping("/{allergyId}")
    fun update(
        @PathVariable studentId: Long,
        @PathVariable allergyId: Long,
        @Valid @RequestBody request: StudentAllergyUpdateRequest
    ): StudentAllergyResponse =
        allergyService.update(
            studentId,
            allergyId,
            request
        )

    @DeleteMapping("/{allergyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable studentId: Long,
        @PathVariable allergyId: Long
    ) {
        allergyService.delete(
            studentId,
            allergyId
        )
    }
}