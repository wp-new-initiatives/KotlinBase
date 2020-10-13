package com.whitepages.kotlinproject.filter

import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(FilterOrder.EXCEPTION_HANDLER_FILTER)
class ExceptionHandlerFilter : OncePerRequestFilter() {

    companion object {
        private val exceptionLogger = LogManager.getLogger(ExceptionHandlerFilter::class.java)
    }

    @Override
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            exceptionLogger.error("Middleware encountered an error", e)
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "There was a problem with the request. Check logs for more information.")
        }
    }
}
