package com.issenur.brighttracker.allergy

import com.issenur.brighttracker.error.ApiErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class StudentAllergyExceptionHandler {

    @ExceptionHandler(StudentAllergyNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(
        exception: StudentAllergyNotFoundException,
        request: HttpServletRequest
    ): ApiErrorResponse =
        ApiErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = exception.message.orEmpty(),
            path = request.requestURI
        )

    @ExceptionHandler(StudentAllergyAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleAlreadyExists(
        exception: StudentAllergyAlreadyExistsException,
        request: HttpServletRequest
    ): ApiErrorResponse =
        ApiErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = HttpStatus.CONFLICT.reasonPhrase,
            message = exception.message.orEmpty(),
            path = request.requestURI
        )
}