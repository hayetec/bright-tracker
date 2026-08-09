package com.issenur.brighttracker.staff

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/staff")
class StaffController(
    private val staffService: StaffService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: StaffRequest
    ): StaffResponse =
        staffService.create(request)

    @GetMapping
    fun findAll(): List<StaffResponse> =
        staffService.findAll()

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long
    ): StaffResponse =
        staffService.findById(id)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: StaffRequest
    ): StaffResponse =
        staffService.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long
    ) {
        staffService.delete(id)
    }
}