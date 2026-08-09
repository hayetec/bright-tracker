package com.issenur.brighttracker.classroom

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/classrooms")
class ClassroomController(
    private val classroomService: ClassroomService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: ClassroomRequest
    ): ClassroomResponse =
        classroomService.create(request)

    @GetMapping
    fun findAll(): List<ClassroomResponse> =
        classroomService.findAll()

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long
    ): ClassroomResponse =
        classroomService.findById(id)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: ClassroomRequest
    ): ClassroomResponse =
        classroomService.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long
    ) {
        classroomService.delete(id)
    }
}