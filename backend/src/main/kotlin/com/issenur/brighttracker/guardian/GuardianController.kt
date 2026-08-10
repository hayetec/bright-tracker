package com.issenur.brighttracker.guardian

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/guardians")
class GuardianController(
    private val guardianService: GuardianService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody request: GuardianRequest
    ): GuardianResponse =
        guardianService.create(request)

    @GetMapping
    fun findAll(): List<GuardianResponse> =
        guardianService.findAll()

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long
    ): GuardianResponse =
        guardianService.findById(id)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: GuardianRequest
    ): GuardianResponse =
        guardianService.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long
    ) {
        guardianService.delete(id)
    }
}