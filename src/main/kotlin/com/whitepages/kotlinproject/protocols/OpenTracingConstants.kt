package com.whitepages.kotlinproject.protocols

object OpenTracingConstants {
        const val TRACKING_ID_SIZE = 32
        const val TRACKING_ID_HEADER = "X-B3-TraceId"

        const val SPAN_ID_SIZE = 16
        const val SPAN_ID_HEADER = "X-B3-SpanId"
    }
