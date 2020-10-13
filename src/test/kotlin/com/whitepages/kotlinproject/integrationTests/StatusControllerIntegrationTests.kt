package com.whitepages.kotlinproject.integrationTests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@AutoConfigureWebClient
class StatusControllerIntegrationTests {

    @Autowired
    lateinit var context: WebApplicationContext

    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .build()
    }

    @Test
    fun testStatusPing() {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/status/ping")
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
