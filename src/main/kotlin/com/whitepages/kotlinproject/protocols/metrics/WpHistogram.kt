package com.whitepages.kotlinproject.protocols.metrics

import com.whitepages.kotlinproject.protocols.metrics.WpMonitor.Companion.MSF
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Histogram
import java.time.Duration

interface WpHistogramInterface {
    fun record(value: Duration, vararg labels: String)
    fun sum(vararg labels: String): Double
}

/**
 * A wrapper around the Histogram metric that prometheus provides
 *
 * @param metricName - Name of the metric
 * @param metricHelp - Help description of the metric
 * @param metricBuckets - List of doubles to be used a buckets for the histogram
 * @param metricLabel - List of labels to be set for this metric
 * @param timeUnit - The time unit to report in
 */
class WpHistogram(
    metricName: String, // Metric name is required as you are not allowed to register metrics with the same name so a defualt name makes no sense
    metricLabels: List<String>, // Metric labels are required (could have no labels)
    metricHelp: String = DEFAULT_HELP,
    metricBuckets: List<Double> = DEFAULT_BUCKETS
) : WpHistogramInterface {
    companion object {
        private val DEFAULT_BUCKETS = listOf(0.0001, 0.00025, 0.0005, 0.00075, 0.001, 0.0025, 0.005, 0.0075, 0.01, 0.025, 0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2, 0.225, 0.25, 0.275, 0.3, 0.325, 0.35, 0.4, 0.45, 0.5, 0.6, 0.7, 1.0, 2.0, 3.0)
        val DEFAULT_CLIENT_BUCKETS = listOf(0.0001, 0.00025, 0.0005, 0.00075, 0.001, 0.0025, 0.005, 0.0075, 0.01, 0.025, 0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2, 0.225, 0.25, 0.275, 0.3, 0.325, 0.35, 0.4, 0.45, 0.5, 0.6, 0.7, 1.0, 2.0, 3.0, 10.0)
        private const val DEFAULT_HELP = "Request Latency"
    }

    private val histogram = Histogram.build()
            .name("$MSF$metricName")
            .help(metricHelp)
            .labelNames(*metricLabels.toTypedArray())
            .buckets(*metricBuckets.toDoubleArray())
            .register(CollectorRegistry.defaultRegistry)

    override fun record(value: Duration, vararg labels: String) {
        histogram.labels(*labels).observe(value.toMillis() / (1000.0 * 60)) // our scale is in seconds
    }

    override fun sum(vararg labels: String): Double = histogram.labels(*labels).get().sum
}
