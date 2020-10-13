package com.whitepages.kotlinproject.filter

import org.springframework.core.Ordered

object FilterOrder {
    /**
     * Ordered.HIGHEST_PRECEDENCE is Integer.MIN_VALUE, so this is ordered like you're golfing...
     */
    const val TIMEOUT_FILTER = Ordered.HIGHEST_PRECEDENCE
    const val LOGGING_CONTEXT_FILTER = Ordered.HIGHEST_PRECEDENCE + 1
    const val METRIC_FILTER = Ordered.HIGHEST_PRECEDENCE + 2
    const val CLIENT_EXCEPTION_HANDLER = Ordered.HIGHEST_PRECEDENCE + 3
    const val EXCEPTION_HANDLER_FILTER = Ordered.HIGHEST_PRECEDENCE + 4
    const val REQUEST_RESPONSE_LOGGING_FILTER = Ordered.HIGHEST_PRECEDENCE + 5
}
