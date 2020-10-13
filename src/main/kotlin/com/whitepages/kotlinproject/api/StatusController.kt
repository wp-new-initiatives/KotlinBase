package com.whitepages.kotlinproject.api

import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusController {
    // currently used in our helm-coresite repo
    @GetMapping(value = ["/status/ping"])
    fun ping(response: HttpServletResponse): HttpStatus = HttpStatus.OK
}
