package com.whitepages.kotlinproject.filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.client.HttpClientErrorException

class ClientExceptionHandlerFilterTests {

    lateinit var subject: ClientExceptionHandlerFilter
    lateinit var request: MockHttpServletRequest
    lateinit var response: MockHttpServletResponse

    @BeforeEach
    fun setUp() {
        subject = ClientExceptionHandlerFilter()
        request = MockHttpServletRequest(HttpMethod.GET.name, "/addOne?inNumber=6")
        response = MockHttpServletResponse()
    }

    private fun testClientException(ex: HttpClientErrorException): ResponseEntity<ApiError> {
        return try {
            subject.generalClientException(ex)
        } catch (ex2: Exception) {
            throw IllegalStateException("handleException threw exception", ex2)
        }
    }

    private fun testCAAException(ex: ProjectException): ResponseEntity<ApiError> {
        return try {
            subject.projectException(ex)
        } catch (ex2: Exception) {
            throw IllegalStateException("handleException threw exception", ex2)
        }
    }

    @Test
    fun testGeneralClientError() {
        val r = testClientException(HttpClientErrorException(BAD_REQUEST))
        assertThat(r.statusCode).isEqualTo(BAD_REQUEST)
        assertThat(r.body!!.errors).isNull()
    }

    @Test
    fun testCaaError() {
        val r = testCAAException(ProjectException(NOT_FOUND, mapOf("Something" to "something else")))
        assertThat(r.statusCode).isEqualTo(NOT_FOUND)
        assertThat(r.body!!.errors).isEqualTo(mapOf("Something" to "something else"))
    }
}
