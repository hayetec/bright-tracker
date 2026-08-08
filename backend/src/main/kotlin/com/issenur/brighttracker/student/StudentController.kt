package com.issenur.brighttracker.student

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/students")
class StudentController(
    private val studentService: StudentService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: StudentRequest
    ): StudentResponse =
        studentService.create(request)

    @GetMapping
    fun findAll(): List<StudentResponse> =
        studentService.findAll()

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long
    ): StudentResponse =
        studentService.findById(id)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: StudentRequest
    ): StudentResponse =
        studentService.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long
    ) {
        studentService.delete(id)
    }
}