package com.whitepages.kotlinproject.filter

import com.whitepages.kotlinproject.AppProperties
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(FilterOrder.TIMEOUT_FILTER)
class TimeoutFilter @Autowired constructor(appProperties: AppProperties) : OncePerRequestFilter() {
    var incomingRequestTimeoutMs: Long = appProperties.incomingRequestTimeoutMs.toLong()

    companion object {
        private val timeoutsPool = Executors.newScheduledThreadPool(10)
        private val timeoutLogger = LogManager.getLogger(TimeoutFilter::class.java)
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val completed = AtomicBoolean(false)
        val requestHandlingMethod = Thread.currentThread()
        val timeout = timeoutsPool.schedule({
            if (completed.compareAndSet(false, true)) {
                requestHandlingMethod.interrupt()
                timeoutLogger.error("The request timed out")
                throw TimeoutException("The request timed out")
            }
        }, incomingRequestTimeoutMs, TimeUnit.MILLISECONDS)

        try {
            filterChain.doFilter(request, response)
            timeout.cancel(false)
        } finally {
            completed.set(true)
        }
    }
}
