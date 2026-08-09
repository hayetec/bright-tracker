package com.issenur.brighttracker.student

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class StudentExceptionHandler {

    @ExceptionHandler(StudentNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleStudentNotFound(
        exception: StudentNotFoundException
    ): Map<String, String> =
        mapOf("message" to exception.message.orEmpty())
}