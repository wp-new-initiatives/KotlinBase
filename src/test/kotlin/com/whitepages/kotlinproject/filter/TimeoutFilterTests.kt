package com.whitepages.kotlinproject.filter

import com.whitepages.kotlinproject.AppProperties
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.resolve
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

@SpringBootTest
@AutoConfigureWebClient
class TimeoutFilterTests @Autowired constructor(var appProperties: AppProperties) {

    lateinit var timeoutFilter: TimeoutFilter
    lateinit var request: MockHttpServletRequest
    lateinit var response: MockHttpServletResponse

    @BeforeEach
    fun setUp() {
        timeoutFilter = TimeoutFilter(appProperties)
        request = MockHttpServletRequest(HttpMethod.GET.name, "/addOne?inNumber=6")
        response = MockHttpServletResponse()
    }

    @Test
    fun testTimeoutFilter() {
        val filterChain = FilterChain { _: ServletRequest?, _: ServletResponse? ->
            // do nothing
        }

        timeoutFilter.doFilter(request, response, filterChain)

        assertThat(resolve(response.status)).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun testTimeoutFilterWithSleepGreaterThanTimeout() {
        val filterChain = FilterChain { _: ServletRequest?, _: ServletResponse? ->
            // sleep for an amount just larger than the timeout.
            Thread.sleep(appProperties.incomingRequestTimeoutMs.toLong() + 10)
        }

        assertThatExceptionOfType(InterruptedException::class.java).isThrownBy {
            timeoutFilter.doFilter(request, response, filterChain)
        }
    }
}
