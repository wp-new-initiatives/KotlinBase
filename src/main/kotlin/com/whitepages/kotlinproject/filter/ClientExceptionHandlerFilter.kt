package com.whitepages.kotlinproject.filter

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import org.apache.logging.log4j.ThreadContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Order(FilterOrder.CLIENT_EXCEPTION_HANDLER)
@ControllerAdvice
class ClientExceptionHandlerFilter : ResponseEntityExceptionHandler() {

    @ExceptionHandler(HttpClientErrorException::class)
    fun generalClientException(ex: HttpClientErrorException): ResponseEntity<ApiError> {
        return ResponseEntity(ApiError(ex, null), ex.statusCode)
    }

    @ExceptionHandler(ProjectException::class)
    fun projectException(ex: ProjectException): ResponseEntity<ApiError> {
        return ResponseEntity(ApiError(ex, ex.errors), ex.statusCode)
    }
}

class ProjectException(status: HttpStatus, val errors: Any?) : HttpClientErrorException(status)
class ApiError private constructor() {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private val timestamp: LocalDateTime = LocalDateTime.now()
    var debugMessage: String? = null
    var spanId: String? = null
    var trackingId: String? = null
    var errors: Any? = null

    constructor(ex: Throwable, errors: Any?) : this() {
        this.errors = errors
        this.spanId = ThreadContext.get("spanId")
        this.trackingId = ThreadContext.get("trackingId")
        debugMessage = ex.localizedMessage
    }
}
