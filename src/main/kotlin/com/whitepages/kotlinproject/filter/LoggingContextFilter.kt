package com.whitepages.kotlinproject.filter

import com.whitepages.kotlinproject.protocols.OpenTracingConstants.SPAN_ID_HEADER
import com.whitepages.kotlinproject.protocols.OpenTracingConstants.TRACKING_ID_HEADER
import com.whitepages.kotlinproject.protocols.logging.OpenTracingIdGeneratorInterface
import java.util.Optional
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import org.apache.logging.log4j.ThreadContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(FilterOrder.LOGGING_CONTEXT_FILTER)
class LoggingContextFilter : Filter {

    @Autowired
    lateinit var idGenerator: OpenTracingIdGeneratorInterface

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest

        ThreadContext.put("trackingId", Optional.ofNullable(req.getHeader(TRACKING_ID_HEADER)).orElse(idGenerator.generateTrackingId()))
        ThreadContext.put("spanId", Optional.ofNullable(req.getHeader(SPAN_ID_HEADER)).orElse(idGenerator.generateSpanId()))
        ThreadContext.put("category", "common")
        try {
            chain.doFilter(request, response)
        } finally {
        }
    }
}
