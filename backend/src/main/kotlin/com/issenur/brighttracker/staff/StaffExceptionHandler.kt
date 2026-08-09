package com.issenur.brighttracker.staff

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class StaffExceptionHandler {

    @ExceptionHandler(StaffNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleStaffNotFound(
        exception: StaffNotFoundException
    ): Map<String, String> =
        mapOf("message" to exception.message.orEmpty())
}