package com.issenur.brighttracker.assignment

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class StaffAssignmentExceptionHandler {

    @ExceptionHandler(StaffAssignmentNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(
        exception: StaffAssignmentNotFoundException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )

    @ExceptionHandler(StaffAssignmentAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleAlreadyExists(
        exception: StaffAssignmentAlreadyExistsException
    ): Map<String, String> =
        mapOf(
            "message" to exception.message.orEmpty()
        )

    @ExceptionHandler(InvalidClassroomStaffRoleException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidRole(
        exception: InvalidClassroomStaffRoleException
    ): Map<String, String> =
        mapOf("message" to exception.message.orEmpty()
        )
}