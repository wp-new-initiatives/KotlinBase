package com.whitepages.kotlinproject.integrationTests

import com.whitepages.kotlinproject.filter.MetricFilter
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@AutoConfigureWebClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MetricsIntegrationTests {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var metricFilter: MetricFilter

    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .addFilter<DefaultMockMvcBuilder>(this.metricFilter, "/*")
                .build()
    }

    @Test
    fun testSingleRequest() {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/actuator/health")
        ).andReturn()
        mockMvc.perform(MockMvcRequestBuilders
                .get("/actuator/prometheus")
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string(StringContains(true, "MSF_request_counter{app=\"kotlinproject-test\",method=\"GET\",uri=\"/actuator/health\",status=\"200 OK\",} 1.0")))
    }

    @Test
    fun testMultipleRequest() {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/actuator/metrics")
        ).andReturn()
        mockMvc.perform(MockMvcRequestBuilders
                .get("/actuator/metrics")
        ).andReturn()

        mockMvc.perform(MockMvcRequestBuilders
                .get("/actuator/prometheus")
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string(StringContains(true, "MSF_request_counter{app=\"kotlinproject-test\",method=\"GET\",uri=\"/actuator/metrics\",status=\"200 OK\",} 2.0")))
                .andExpect(MockMvcResultMatchers.content().string(StringContains(true, "MSF_latency_seconds_bucket{app=\"kotlinproject-test\",method=\"GET\",uri=\"/actuator/metrics\",status=\"200 OK\",le=\"+Inf\",} 2.0\n")))
    }
}
