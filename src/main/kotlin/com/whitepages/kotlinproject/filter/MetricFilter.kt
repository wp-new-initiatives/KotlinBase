package com.whitepages.kotlinproject.filter

import com.whitepages.kotlinproject.AppProperties
import com.whitepages.kotlinproject.protocols.metrics.WpCounterInterface
import com.whitepages.kotlinproject.protocols.metrics.WpHistogramInterface
import java.time.Duration
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(FilterOrder.METRIC_FILTER)
class MetricFilter @Autowired constructor(appProperties: AppProperties) : OncePerRequestFilter() {
    var appName: String = appProperties.appName

    @Autowired
    lateinit var latencyHistogram: WpHistogramInterface

    @Autowired
    lateinit var requestCounter: WpCounterInterface

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        val startTime = System.currentTimeMillis()
        try {
            filterChain.doFilter(request, response)
        } finally {
            val completeTime = System.currentTimeMillis() - startTime
            latencyHistogram.record(Duration.ofMillis(completeTime), appName, request.method, request.requestURI, HttpStatus.valueOf(response.status).toString())
            requestCounter.incrementCounter(appName, request.method, request.requestURI, HttpStatus.valueOf(response.status).toString())
        }
    }
}
