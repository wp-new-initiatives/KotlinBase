package com.whitepages.kotlinproject.protocols.metrics

import com.whitepages.kotlinproject.protocols.metrics.WpMonitor.Companion.MSF
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Counter

interface WpCounterInterface {
    fun incrementCounter(vararg labels: String)
    fun incrementCounterN(n: Double, vararg labels: String)
    fun get(vararg labels: String): Double
}

/**
 * A wrapper around the counter metric that prometheus provides
 *
 * @param metricName - Name of the metric
 * @param metricHelp - Help description of the metric
 * @param metricLabel - List of labels to be set for this metric
 */
class WpCounter(
    metricName: String,
    metricLabel: List<String>,
    metricHelp: String = DEFAULT_HELP
) : WpCounterInterface {
    companion object {
        private const val DEFAULT_HELP = "Request Count"
    }

    private val counter = Counter.build()
            .name("$MSF$metricName")
            .help(metricHelp)
            .labelNames(*metricLabel.toTypedArray())
            .register(CollectorRegistry.defaultRegistry)

    override fun incrementCounter(vararg labels: String) {
        incrementCounterN(1.0, *labels)
    }

    override fun incrementCounterN(n: Double, vararg labels: String) {
        counter.labels(*labels).inc(n)
    }

    override fun get(vararg labels: String): Double = counter.labels(*labels).get()
}
