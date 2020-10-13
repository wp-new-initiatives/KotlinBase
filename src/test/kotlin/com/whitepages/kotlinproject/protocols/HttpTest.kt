package com.whitepages.kotlinproject.protocols

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import com.whitepages.kotlinproject.AppProperties
import com.whitepages.kotlinproject.protocols.metrics.WpCounterInterface
import com.whitepages.kotlinproject.protocols.metrics.WpHistogramInterface
import java.net.URI
import java.time.Duration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

@SpringBootTest
@AutoConfigureWebClient
internal class HttpTest {

    @Mock
    lateinit var restTemplate: RestTemplate

    @Mock
    lateinit var appProperties: AppProperties

    @Mock
    lateinit var clientHistogram: WpHistogramInterface

    @Mock
    lateinit var clientCounter: WpCounterInterface

    @Autowired
    @InjectMocks
    lateinit var http: Http

    @BeforeEach
    fun beforeEach() {
        MockitoAnnotations.initMocks(this)
    }

    fun setupMock(status: HttpStatus, method: HttpMethod = HttpMethod.POST, uri: String = "http://example.com") {
        whenever(restTemplate.exchange<String>(
                URI(uri),
                method,
                HttpEntity("", null),
                ParameterizedTypeReference.forType<String>(String::class.java)
        )).thenReturn(ResponseEntity<String>(status))
    }

    fun assertHit(times: Int, method: HttpMethod = HttpMethod.POST, uri: String = "http://example.com") {
        Mockito.verify(restTemplate, times(times)).exchange<String>(
                URI(uri),
                method,
                HttpEntity("", null),
                ParameterizedTypeReference.forType<String>(String::class.java)
        )
    }

    @Test
    fun initialization() {
        Assertions.assertNotNull(http)
    }

    @Test
    fun testPost() {
        setupMock(HttpStatus.OK)
        val response = http.post<String>("client", "http://example.com", mapOf(), null, "", ParameterizedTypeReference.forType(String::class.java))
        assertHit(1)

        Assertions.assertTrue(response.statusCode.is2xxSuccessful)
    }

    @Test
    fun testGet() {
        setupMock(HttpStatus.OK, HttpMethod.GET, "http://example.com?param=value")

        val response = http.get<String>("client", "http://example.com", mapOf("param" to "value"), null, ParameterizedTypeReference.forType(String::class.java))
        assertHit(1, HttpMethod.GET, "http://example.com?param=value")

        Assertions.assertTrue(response.statusCode.is2xxSuccessful)
    }

    @Test
    fun testGetListParam() {
        setupMock(HttpStatus.OK, HttpMethod.GET, "http://example.com?param=value&param=value2")

        val response = http.get<String>("client", "http://example.com", mapOf("param" to arrayListOf("value", "value2")), null, ParameterizedTypeReference.forType(String::class.java))
        assertHit(1, HttpMethod.GET, "http://example.com?param=value&param=value2")

        Assertions.assertTrue(response.statusCode.is2xxSuccessful)
    }

    @Test
    fun testPut() {
        setupMock(HttpStatus.OK, HttpMethod.PUT)
        val response = http.put<String>("client", "http://example.com", null, "", ParameterizedTypeReference.forType(String::class.java))
        assertHit(1, HttpMethod.PUT)

        Assertions.assertTrue(response.statusCode.is2xxSuccessful)
    }

    @Test
    fun testDelete() {
        setupMock(HttpStatus.OK, HttpMethod.DELETE)
        val response = http.delete<String>("client", "http://example.com", null, "", ParameterizedTypeReference.forType(String::class.java))
        assertHit(1, HttpMethod.DELETE)

        Assertions.assertTrue(response.statusCode.is2xxSuccessful)
    }

    @Test
    fun testMetricCounter() {
        setupMock(HttpStatus.OK)
        val response = http.post<String>("client", "http://example.com", mapOf(), null, "", ParameterizedTypeReference.forType(String::class.java))
        assertHit(1)
        Assertions.assertTrue(response.statusCode.is2xxSuccessful)
        Mockito.verify(clientCounter, times(1)).incrementCounter("kotlinproject-test", "client", "200 OK")
    }

    @Test
    fun testMetricHistogram() {
        setupMock(HttpStatus.OK)
        val response = http.post<String>("client", "http://example.com", mapOf(), null, "", ParameterizedTypeReference.forType(String::class.java))
        assertHit(1)
        Assertions.assertTrue(response.statusCode.is2xxSuccessful)
        val duration = com.nhaarman.mockitokotlin2.argumentCaptor<Duration>()
        val appName = com.nhaarman.mockitokotlin2.argumentCaptor<String>()
        val clientName = com.nhaarman.mockitokotlin2.argumentCaptor<String>()
        val statusName = com.nhaarman.mockitokotlin2.argumentCaptor<String>()
        Mockito.verify(clientHistogram, times(1)).record(duration.capture(), appName.capture(), clientName.capture(), statusName.capture())
        // is 10 milliseconds enough leeway time?
        Assertions.assertTrue(duration.firstValue.toMillis() <= 10)
        Assertions.assertEquals(appName.firstValue, "kotlinproject-test")
        Assertions.assertEquals(clientName.firstValue, "client")
        Assertions.assertEquals(statusName.firstValue, "200 OK")
    }
}
