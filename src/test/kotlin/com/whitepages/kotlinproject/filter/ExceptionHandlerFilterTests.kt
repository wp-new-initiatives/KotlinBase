package com.whitepages.kotlinproject.filter

import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.resolve
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class ExceptionHandlerFilterTests {

    lateinit var exceptionFilter: ExceptionHandlerFilter
    lateinit var request: MockHttpServletRequest
    lateinit var response: MockHttpServletResponse

    @BeforeEach
    fun setUp() {
        exceptionFilter = ExceptionHandlerFilter()
        request = MockHttpServletRequest(HttpMethod.GET.name, "/addOne?inNumber=6")
        response = MockHttpServletResponse()
    }

    @Test
    fun testTimeoutFilter() {
        val filterChain = FilterChain { _: ServletRequest?, _: ServletResponse? ->
            // do nothing
        }

        exceptionFilter.doFilter(request, response, filterChain)

        Assertions.assertThat(resolve(response.status)).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun testTimeoutFilterWithSleepGreaterThanTimeout() {
        val filterChain = FilterChain { _: ServletRequest?, _: ServletResponse? ->
            throw Exception()
        }

        exceptionFilter.doFilter(request, response, filterChain)

        Assertions.assertThat(resolve(response.status)).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        Assertions.assertThat(response.errorMessage).isEqualTo("There was a problem with the request. Check logs for more information.")
    }
}
