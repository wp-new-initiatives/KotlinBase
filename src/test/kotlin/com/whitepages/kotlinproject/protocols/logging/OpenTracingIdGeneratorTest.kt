package com.whitepages.kotlinproject.protocols.logging

import com.nhaarman.mockitokotlin2.whenever
import java.lang.IllegalArgumentException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureWebClient
class OpenTracingIdGeneratorTest {
    @Mock
    lateinit var uuidGen: UuidGeneratorInterface

    @Autowired
    @InjectMocks
    lateinit var hexGen: OpenTracingIdGenerator

    @BeforeEach
    fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        whenever(uuidGen.generateRandomUuid()).thenReturn("fc986548-fc22-4fc3-b873-0a2bcbef9876")
    }

    @Test
    fun initialization() {
        Assertions.assertNotNull(hexGen)
    }

    @Test
    fun testGenerateRandom32CharHex() {
        val generatedHex = hexGen.generateHex(32)
        Assertions.assertEquals("fc986548fc224fc3b8730a2bcbef9876", generatedHex)
    }
    @Test
    fun testGenerateRandomLessThan32CharHex() {
        val generatedHex = hexGen.generateHex(16)
        Assertions.assertEquals("fc986548fc224fc3", generatedHex)
    }
    @Test
    fun testGenerateRandomGreaterThan32CharHex() {
        val generatedHex = hexGen.generateHex(48)
        Assertions.assertEquals("fc986548fc224fc3b8730a2bcbef9876fc986548fc224fc3", generatedHex)
    }
    @Test
    fun testGeneratingRandomLessThanZeroCharHexThrowsIllegalArgumentException() {
        assertThrows<IllegalArgumentException> {
            hexGen.generateHex(-2)
        }
    }

    @Test
    fun testGenerateTrackingId() {
        val generatedHex = hexGen.generateHex(32)
        Assertions.assertEquals(hexGen.generateTrackingId(), generatedHex)
    }

    @Test
    fun testGenerateSpanId() {
        val generatedHex = hexGen.generateHex(16)
        Assertions.assertEquals(hexGen.generateSpanId(), generatedHex)
    }
}
