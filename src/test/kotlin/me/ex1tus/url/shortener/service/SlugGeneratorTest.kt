package me.ex1tus.url.shortener.service

import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import me.ex1tus.url.shortener.config.property.SlugProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SlugGeneratorTest {

    private val slugGenerator: SlugGenerator

    private val slugProperties = SlugProperties(7, "123456789")
    private val random: Random = mockk()

    init {
        slugGenerator = SlugGenerator(slugProperties, random)
    }

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    inner class Generate {

        @Test
        fun `generates slug of a given length using given alphabet`() {
            // Given
            every { random.nextInt(any()) } returns 0 andThenMany listOf(1, 2, 3, 4, 5, 6)

            // When
            val slug = slugGenerator.generate()

            // Then
            slug shouldBe "1234567"
        }
    }
}
