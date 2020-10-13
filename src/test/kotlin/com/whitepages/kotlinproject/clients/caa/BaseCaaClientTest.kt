package com.whitepages.kotlinproject.clients.caa

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import com.whitepages.kotlinproject.AppProperties
import com.whitepages.kotlinproject.filter.ProjectException
import com.whitepages.kotlinproject.presenters.consumerApps.BaseCaaResponse
import com.whitepages.kotlinproject.protocols.HttpInterface
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

@SpringBootTest
@AutoConfigureWebClient
internal class BaseCaaClientTest {

    data class MockCaaResponseMap(
        override val statusCode: Int,
        override val result: Map<String, String>,
        override val errors: Map<String, Any>?
    ) : BaseCaaResponse(statusCode, result, errors)

    data class MockCaaResponseString(
        override val statusCode: Int,
        override val result: String,
        override val errors: Map<String, Any>?
    ) : BaseCaaResponse(statusCode, result, errors)

    @Mock
    lateinit var http: HttpInterface

    @Mock
    lateinit var appProperties: AppProperties

    @Autowired
    @InjectMocks
    lateinit var subject: BaseCaaClient

    @BeforeEach
    fun beforeEach() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun initialization() {
        assertNotNull(subject)
    }

    fun callMap(response: Map<String, String>, statusCode: Int = 200, errors: Map<String, Any>? = null) {
    }

    fun putMap(response: Map<String, String>, statusCode: Int = 200, errors: Map<String, Any>? = null) {
        whenever(http.put<MockCaaResponseMap>(
                client = any(),
                url = any(),
                headers = any(),
                body = any(),
                returnType = any()
        )).thenReturn(ResponseEntity(MockCaaResponseMap(statusCode, response, errors), HttpStatus.OK))
    }

    fun callString(response: String, statusCode: Int = 200, errors: Map<String, Any>? = null) {
        whenever(http.get<MockCaaResponseString>(
                client = any(),
                url = any(),
                params = any(),
                headers = any(),
                shouldEncode = any(),
                returnType = any()
        )).thenReturn(ResponseEntity(MockCaaResponseString(statusCode, response, errors), HttpStatus.OK))
    }

    @Test
    fun testSuccessfulGet() {
        whenever(http.get<MockCaaResponseString>(
                client = "consumer apps base client - test",
                url = "example.com/magical-endpoint",
                params = mapOf("test" to "thing"),
                headers = HttpHeaders().apply {},
                shouldEncode = true,
                returnType = ParameterizedTypeReference.forType(MockCaaResponseString::class.java)
        )).thenReturn(ResponseEntity(MockCaaResponseString(200, "Im a string", null), HttpStatus.OK))
        val response = subject.get<MockCaaResponseString, String>("test", "magical-endpoint", mapOf("test" to "thing"), true, ParameterizedTypeReference.forType(MockCaaResponseString::class.java))
        assertEquals(response, "Im a string")
    }

    @Test
    fun testSuccessfulGetWithTypes() {
        whenever(http.get<MockCaaResponseMap>(
                client = "consumer apps base client - test",
                url = "example.com/magical-endpoint",
                params = mapOf("test" to "thing"),
                headers = HttpHeaders().apply {},
                shouldEncode = true,
                returnType = ParameterizedTypeReference.forType(MockCaaResponseMap::class.java)
        )).thenReturn(ResponseEntity(MockCaaResponseMap(200, mapOf("but" to "I can also be a map!"), null), HttpStatus.OK))

        val response = subject.get<MockCaaResponseMap, Map<String, String>>("test", "magical-endpoint", mapOf("test" to "thing"), true, ParameterizedTypeReference.forType(MockCaaResponseMap::class.java))
        assertEquals(response, mapOf("but" to "I can also be a map!"))
    }

    @Test
    fun testSuccessfulPutWithTypes() {
        putMap(mapOf("but" to "I can also be a map!"))
        val response = subject.put<MockCaaResponseMap, Map<String, String>>("test", "magical-endpoint", listOf("test" to "thing"), ParameterizedTypeReference.forType(MockCaaResponseMap::class.java))
        assertEquals(response, mapOf("but" to "I can also be a map!"))
    }

    @Test
    fun testSuccessfulPut_RemovesNullFromParamsWithTypes() {
        whenever(http.put<MockCaaResponseMap>(
                client = "consumer apps base client - test",
                url = "example.com/magical-endpoint",
                headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED },
                body = "test=thing",
                returnType = ParameterizedTypeReference.forType(MockCaaResponseMap::class.java)
        )).thenReturn(ResponseEntity(MockCaaResponseMap(200, mapOf("success" to "withoutNull"), null), HttpStatus.OK))

        val response = subject.put<MockCaaResponseMap, Map<String, String>>("test", "magical-endpoint", listOf("test" to "thing", "empty" to null), ParameterizedTypeReference.forType(MockCaaResponseMap::class.java))
        assertEquals(response, mapOf("success" to "withoutNull"))
    }

    @Test
    fun testSuccessfulPost_RemovesNullFromParamsWithTypes() {
        whenever(http.post<MockCaaResponseMap>(
                client = "consumer apps base client - test",
                url = "example.com/magical-endpoint",
                params = mapOf(),
                headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED },
                body = "test=thing",
                returnType = ParameterizedTypeReference.forType(MockCaaResponseMap::class.java)
        )).thenReturn(ResponseEntity(MockCaaResponseMap(200, mapOf("success" to "withoutNull"), null), HttpStatus.OK))

        val response = subject.post<MockCaaResponseMap, Map<String, String>>("test", "magical-endpoint", listOf("test" to "thing", "empty" to null), ParameterizedTypeReference.forType(MockCaaResponseMap::class.java))
        assertEquals(response, mapOf("success" to "withoutNull"))
    }

    @Test
    fun testSuccessfulPostWithTypes() {
        whenever(http.post<MockCaaResponseMap>(
                client = "consumer apps base client - test",
                url = "example.com/magical-endpoint",
                params = mapOf(),
                headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED },
                body = "test=thing",
                returnType = ParameterizedTypeReference.forType(MockCaaResponseMap::class.java)
        )).thenReturn(ResponseEntity(MockCaaResponseMap(200, mapOf("but" to "I can also be a map!"), null), HttpStatus.OK))
        val response = subject.post<MockCaaResponseMap, Map<String, String>>("test", "magical-endpoint", listOf("test" to "thing"), ParameterizedTypeReference.forType(MockCaaResponseMap::class.java))
        assertEquals(response, mapOf("but" to "I can also be a map!"))
    }

    @Test
    fun testSuccessfulPostWithBodyAsString() {
        whenever(http.post<MockCaaResponseMap>(
                client = "consumer apps base client - test",
                url = "example.com/magical-endpoint",
                params = mapOf(),
                headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED },
                body = "test=thing",
                returnType = ParameterizedTypeReference.forType(MockCaaResponseMap::class.java)
        )).thenReturn(ResponseEntity(MockCaaResponseMap(200, mapOf("but" to "I can also be a map!"), null), HttpStatus.OK))
        val response = subject.post<MockCaaResponseMap, Map<String, String>>("test", "magical-endpoint", "test=thing", ParameterizedTypeReference.forType(MockCaaResponseMap::class.java))
        assertEquals(response, mapOf("but" to "I can also be a map!"))
    }

    @Test
    fun testFailingGetReturnsError() {
        whenever(http.get<MockCaaResponseMap>(
                client = "consumer apps base client - test",
                url = "example.com/magical-endpoint",
                params = mapOf("test" to "thing"),
                headers = HttpHeaders().apply {},
                shouldEncode = true,
                returnType = ParameterizedTypeReference.forType(MockCaaResponseMap::class.java)
        )).thenReturn(ResponseEntity(MockCaaResponseMap(400, mapOf("throws" to "Exception!"), mapOf("Error" to "Oh no")), HttpStatus.OK))
        val thrown = assertThrows(
                ProjectException::class.java,
                {
                    subject.get<MockCaaResponseMap, Map<String, String>>("test", "magical-endpoint", mapOf("test" to "thing"), true, ParameterizedTypeReference.forType(MockCaaResponseMap::class.java)
                ) },
                "Expected get() to throw, but it didn't"
        )
        assertEquals(thrown.errors, mapOf("Error" to "Oh no"))
    }
}
