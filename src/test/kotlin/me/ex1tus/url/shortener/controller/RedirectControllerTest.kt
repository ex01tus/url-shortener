package me.ex1tus.url.shortener.controller

import io.mockk.*
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest
class RedirectControllerTest {

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
    fun `redirects user to original long link`() {
        // Given
        val slug = "slug"
        val longLink = "https://redirect.com"
        every { service.getLongLink(slug) } returns "https://redirect.com"

        // When - Then
        mvc.perform(MockMvcRequestBuilders.get("/$slug"))
            .andExpect(status().isFound)
            .andExpect(MockMvcResultMatchers.header().string("location", longLink))
    }

    @Test
    fun `returns not found response if no slug was found`() {
        // Given
        val slug = "slug"
        every { service.getLongLink(slug) } throws SlugNotFoundException(slug)

        // When - Then
        mvc.perform(MockMvcRequestBuilders.get("/$slug"))
            .andExpect(status().isNotFound)
    }
}