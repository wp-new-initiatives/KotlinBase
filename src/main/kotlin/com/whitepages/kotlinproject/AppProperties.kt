package com.whitepages.kotlinproject

import com.whitepages.kotlinproject.protocols.logging.UuidGenerator
import com.whitepages.kotlinproject.protocols.logging.UuidGeneratorInterface
import com.whitepages.kotlinproject.protocols.metrics.WpCounter
import com.whitepages.kotlinproject.protocols.metrics.WpCounterInterface
import com.whitepages.kotlinproject.protocols.metrics.WpHistogram
import com.whitepages.kotlinproject.protocols.metrics.WpHistogramInterface
import io.prometheus.client.CollectorRegistry
import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Component
class AppProperties : WebMvcConfigurer {

    @Value("\${com.whitepages.app-name}")
    lateinit var appName: String
    @Value("\${consumer-apps-url}")
    lateinit var consumerAppsUrl: String
    @Value("\${request.timeout}")
    lateinit var incomingRequestTimeoutMs: String

    @Bean
    fun prometheusCollector(): CollectorRegistry = CollectorRegistry.defaultRegistry

    @Bean
    fun restTemplate(): RestTemplate = RestTemplateBuilder().build()

    @Bean
    fun latencyHistogram(): WpHistogramInterface = WpHistogram("latency_seconds", listOf("app", "method", "uri", "status"))

    @Bean
    fun requestCounter(): WpCounterInterface = WpCounter("request_counter", listOf("app", "method", "uri", "status"))

    @Bean
    fun clientHistogram(): WpHistogramInterface = WpHistogram("client_histogram_default", listOf("app", "client", "status"), "client latency", WpHistogram.DEFAULT_CLIENT_BUCKETS)

    @Bean
    fun clientCounter(): WpCounterInterface = WpCounter("client_counter_default", listOf("app", "client", "status"), "client counter")

    @Bean
    fun uuidGenerator(): UuidGeneratorInterface = UuidGenerator()

    @Bean
    fun restTemplateReadTimeout(builder: RestTemplateBuilder): RestTemplate {
        // zuora takes a while...
        return builder
                .setReadTimeout(Duration.ofSeconds(15))
                .build()
    }
}
