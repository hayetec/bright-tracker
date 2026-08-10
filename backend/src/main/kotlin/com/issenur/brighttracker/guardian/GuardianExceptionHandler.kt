package com.issenur.brighttracker.guardian

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GuardianExceptionHandler {

    @ExceptionHandler(GuardianNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleGuardianNotFound(
        exception: GuardianNotFoundException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )

    @ExceptionHandler(StudentGuardianNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleRelationshipNotFound(
        exception: StudentGuardianNotFoundException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )

    @ExceptionHandler(StudentGuardianAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleRelationshipAlreadyExists(
        exception: StudentGuardianAlreadyExistsException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )
}