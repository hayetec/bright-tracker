package com.issenur.brighttracker.assignment

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AssignmentExceptionHandler {

    @ExceptionHandler(AssignmentNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(
        exception: AssignmentNotFoundException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )

    @ExceptionHandler(AssignmentAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleAlreadyExists(
        exception: AssignmentAlreadyExistsException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )

    @ExceptionHandler(AssignmentInvalidRoleException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidRole(
        exception: AssignmentInvalidRoleException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )
}