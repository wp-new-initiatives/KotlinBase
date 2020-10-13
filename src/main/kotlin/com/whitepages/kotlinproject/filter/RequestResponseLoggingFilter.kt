package com.whitepages.kotlinproject.filter

import com.whitepages.kotlinproject.protocols.AccessMessage
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(FilterOrder.REQUEST_RESPONSE_LOGGING_FILTER)
class RequestResponseLoggingFilter : Filter {

    companion object {
        private val requestResponseLogger = LogManager.getLogger(RequestResponseLoggingFilter::class.java)
    }

    override fun init(filterConfig: FilterConfig) {
        requestResponseLogger.info("Initializing filter")
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val startTime = System.currentTimeMillis()
        val req = request as HttpServletRequest
        val res = response as HttpServletResponse

        try {
            chain.doFilter(request, response)
        } finally {
            val completeTime = System.currentTimeMillis()
            // Creating and sending access log object
            val accessMessage = AccessMessage(
                    completeTime - startTime,
                    req.method,
                    "${req.requestURI}?${req.queryString}",
                    res.status
            )
            ThreadContext.put("category", "access")
            requestResponseLogger.info(accessMessage)
            ThreadContext.put("category", "common")
        }
    }

    override fun destroy() {
        requestResponseLogger.warn("Removing filter")
    }
}
