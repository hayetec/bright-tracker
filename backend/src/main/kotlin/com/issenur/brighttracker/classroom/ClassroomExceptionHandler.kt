package com.issenur.brighttracker.classroom

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ClassroomExceptionHandler {

    @ExceptionHandler(ClassroomNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleClassroomNotFound(
        exception: ClassroomNotFoundException
    ): Map<String, String> =
        mapOf("message" to exception.message.orEmpty())
}