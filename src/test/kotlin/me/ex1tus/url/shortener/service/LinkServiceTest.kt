package me.ex1tus.url.shortener.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import me.ex1tus.url.shortener.domain.ShortLinkRequest
import me.ex1tus.url.shortener.domain.ShortLinkResponse
import me.ex1tus.url.shortener.exception.SlugAlreadyExistsException
import me.ex1tus.url.shortener.exception.SlugNotFoundException
import org.junit.jupiter.api.*
import org.springframework.data.redis.core.RedisTemplate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LinkServiceTest {

    private val linkService: LinkService

    private val redisTemplate: RedisTemplate<String, String> = mockk()
    private val slugGenerator: SlugGenerator = mockk()
    private val serverUrl = "http://localhost:8080"

    init {
        linkService = LinkService(redisTemplate, slugGenerator, serverUrl)
    }

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    inner class CreateShortLink {

        @Test
        fun `throws exception if user's slug already exists`() {
            // Given
            val nonUniqueSlug = "slug"

            every { redisTemplate.hasKey(nonUniqueSlug) } returns true

            // When - Then
            val exception = shouldThrow<SlugAlreadyExistsException> {
                linkService.createShortLink(ShortLinkRequest("destination", nonUniqueSlug))
            }

            exception.message shouldBe nonUniqueSlug
        }

        @Test
        fun `uses unique user's slug`() {
            // Given
            val destination = "destination"
            val uniqueSlug = "slug"

            every { redisTemplate.hasKey(uniqueSlug) } returns false
            every { redisTemplate.opsForValue().set(any(), any()) } just Runs

            // When
            val response = linkService.createShortLink(ShortLinkRequest(destination, uniqueSlug))

            // Then
            response shouldBe ShortLinkResponse(destination, "$serverUrl/$uniqueSlug")
            verify(exactly = 1) { redisTemplate.opsForValue().set(uniqueSlug, destination) }
        }

        @Test
        fun `generates new slug`() {
            // Given
            val destination = "destination"
            val uniqueGeneratedSlug = "slug"

            every { redisTemplate.hasKey(uniqueGeneratedSlug) } returns false
            every { redisTemplate.opsForValue().set(any(), any()) } just Runs
            every { slugGenerator.generate() } returns uniqueGeneratedSlug

            // When
            val response = linkService.createShortLink(ShortLinkRequest(destination, null))

            // Then
            response shouldBe ShortLinkResponse(destination, "$serverUrl/$uniqueGeneratedSlug")
            verify(exactly = 1) { redisTemplate.opsForValue().set(uniqueGeneratedSlug, destination) }
        }

        @Test
        fun `re-rolls non-unique generated slug `() {
            // Given
            val destination = "destination"
            val nonUniqueGeneratedSlug = "first-slug"
            val uniqueGeneratedSlug = "second-slug"

            every { redisTemplate.hasKey(nonUniqueGeneratedSlug) } returns true
            every { redisTemplate.hasKey(uniqueGeneratedSlug) } returns false
            every { redisTemplate.opsForValue().set(any(), any()) } just Runs
            every { slugGenerator.generate() } returns nonUniqueGeneratedSlug andThen uniqueGeneratedSlug

            // When
            val response = linkService.createShortLink(ShortLinkRequest(destination, null))

            // Then
            response shouldBe ShortLinkResponse(destination, "$serverUrl/$uniqueGeneratedSlug")
            verify(exactly = 1) { redisTemplate.opsForValue().set(uniqueGeneratedSlug, destination) }
        }
    }

    @Nested
    inner class GetLongLink {

        @Test
        fun `throws exception if slug was not found`() {
            // Given
            val slug = "slug"

            every { redisTemplate.opsForValue().get(slug) } returns null

            // When - Then
            val exception = shouldThrow<SlugNotFoundException> {
                linkService.getLongLink(slug)
            }

            exception.message shouldBe slug
        }

        @Test
        fun `returns original link`() {
            // Given
            val slug = "slug"
            val longLink = "long-link"

            every { redisTemplate.opsForValue().get(slug) } returns longLink

            // When
            val response = linkService.getLongLink(slug)

            // Then
            response shouldBe longLink
        }
    }
}
