package me.ex1tus.url.shortener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import me.ex1tus.url.shortener.domain.ShortLinkRequest
import me.ex1tus.url.shortener.domain.ShortLinkResponse
import me.ex1tus.url.shortener.exception.SlugAlreadyExistsException
import me.ex1tus.url.shortener.exception.SlugNotFoundException
import me.ex1tus.url.shortener.service.LinkService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest
class LinkControllerTest {

    companion object {
        private val MAPPER = ObjectMapper()
    }

    @TestConfiguration
    class TestConfig {

        @Bean
        fun service() = mockk<LinkService>()
    }

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var service: LinkService

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `creates a short link`() {
        // Given
        val request = ShortLinkRequest("destination", "slug")
        val response = ShortLinkResponse("longLink", "shortLink")
        every { service.createShortLink(request) } returns response

        // When - Then
        mvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/link/short")
                .content(request.toJson())
                .header("content-type", "application/json")
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(response.toJson()))
    }

    @Test
    fun `returns bad request response if slug already exists`() {
        // Given
        val request = ShortLinkRequest("destination", "slug")
        every { service.createShortLink(request) } throws SlugAlreadyExistsException(request.slug!!)

        // When - Then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/link/short"))
            .andExpect(status().isBadRequest)
    }

    private fun Any.toJson() = MAPPER.writeValueAsString(this)
}