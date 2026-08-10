package com.issenur.brighttracker.enrollment

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class EnrollmentExceptionHandler {

    @ExceptionHandler(EnrollmentNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleEnrollmentNotFound(
        exception: EnrollmentNotFoundException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )

    @ExceptionHandler(EnrollmentAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleEnrollmentAlreadyExists(
        exception: EnrollmentAlreadyExistsException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )
}