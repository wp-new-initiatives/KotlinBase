package com.whitepages.kotlinproject.protocols

import com.whitepages.kotlinproject.AppProperties
import com.whitepages.kotlinproject.protocols.metrics.WpCounterInterface
import com.whitepages.kotlinproject.protocols.metrics.WpHistogramInterface
import java.net.URI
import java.time.Duration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

interface HttpInterface {
    fun <T> get(client: String, url: String, params: Map<String, Any>, headers: HttpHeaders?, returnType: ParameterizedTypeReference<T>, shouldEncode: Boolean = true): ResponseEntity<T>
    fun <T> post(client: String, url: String, params: Map<String, Any> = mapOf(), headers: HttpHeaders?, body: String, returnType: ParameterizedTypeReference<T>): ResponseEntity<T>
    fun <T> put(client: String, url: String, headers: HttpHeaders?, body: String, returnType: ParameterizedTypeReference<T>): ResponseEntity<T>
    fun <T> delete(client: String, url: String, headers: HttpHeaders?, body: String, returnType: ParameterizedTypeReference<T>): ResponseEntity<T>
}

@Component
class Http @Autowired constructor(appProperties: AppProperties) : HttpInterface {
    val appName: String = appProperties.appName

    @Autowired
    @Qualifier("restTemplateReadTimeout")
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var clientCounter: WpCounterInterface

    @Autowired
    private lateinit var clientHistogram: WpHistogramInterface

    companion object {
        private val httpLogger = LogManager.getLogger(Http::class.java)
    }

    @Throws(HttpClientErrorException::class)
    override fun <T> get(client: String, url: String, params: Map<String, Any>, headers: HttpHeaders?, returnType: ParameterizedTypeReference<T>, shouldEncode: Boolean): ResponseEntity<T> {
        return request(client, url, HttpMethod.GET, params, headers, "", shouldEncode, returnType)
    }

    @Throws(HttpClientErrorException::class)
    override fun <T> post(client: String, url: String, params: Map<String, Any>, headers: HttpHeaders?, body: String, returnType: ParameterizedTypeReference<T>): ResponseEntity<T> {
        return request(client, url, HttpMethod.POST, params, headers, body, true, returnType)
    }

    @Throws(HttpClientErrorException::class)
    override fun <T> put(client: String, url: String, headers: HttpHeaders?, body: String, returnType: ParameterizedTypeReference<T>): ResponseEntity<T> {
        return request(client, url, HttpMethod.PUT, mapOf(), headers, body, true, returnType)
    }

    @Throws(HttpClientErrorException::class)
    override fun <T> delete(client: String, url: String, headers: HttpHeaders?, body: String, returnType: ParameterizedTypeReference<T>): ResponseEntity<T> {
        return request(client, url, HttpMethod.DELETE, mapOf(), headers, body, true, returnType)
    }

    @Throws(HttpClientErrorException::class)
    private fun <T> request(client: String, url: String, method: HttpMethod, params: Map<String, Any>, headers: HttpHeaders?, body: String, shouldEncode: Boolean, returnType: ParameterizedTypeReference<T>): ResponseEntity<T> {
        val urlBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url)
        params.forEach { (k, v) ->
            // this IF statement is intentional and queryParam is a different signature
            if (v is ArrayList<*>) {
                urlBuilder.queryParam(k, v)
            } else {
                urlBuilder.queryParam(k, v)
            }
        }
        val uri: URI = urlBuilder.build(!shouldEncode).toUri()

        val entity: HttpEntity<String> = HttpEntity(body, headers)
        val startTime = System.currentTimeMillis()
        try {
            val response = restTemplate.exchange(uri, method, entity, returnType)
            logClient(client, startTime, uri.toString(), response.statusCodeValue, response.body.toString())
            return response
        } catch (e: HttpClientErrorException) {
            logClient(client, startTime, uri.toString(), e.rawStatusCode, e.responseBodyAsString)
            throw e
        }
    }

    private fun logClient(client: String, startTime: Long, url: String, statusCode: Int, body: String) {
        val duration = System.currentTimeMillis() - startTime

        val clientMessage = ClientMessage(
                client,
                duration,
                url,
                statusCode,
                body
        )
        clientCounter.incrementCounter(appName, client, HttpStatus.valueOf(statusCode).toString())
        clientHistogram.record(Duration.ofMillis(duration), appName, client, HttpStatus.valueOf(statusCode).toString())

        ThreadContext.put("category", "client")
        httpLogger.info(clientMessage)
        ThreadContext.put("category", "common")
    }
}
