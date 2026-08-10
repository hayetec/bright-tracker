package com.issenur.brighttracker.allergy

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class StudentAllergyExceptionHandler {

    @ExceptionHandler(StudentAllergyNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(
        exception: StudentAllergyNotFoundException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )

    @ExceptionHandler(StudentAllergyAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleAlreadyExists(
        exception: StudentAllergyAlreadyExistsException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )
}