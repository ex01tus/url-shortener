package me.ex1tus.url.shortener.service

import me.ex1tus.url.shortener.domain.ShortLinkRequest
import me.ex1tus.url.shortener.domain.ShortLinkResponse
import me.ex1tus.url.shortener.exception.SlugAlreadyExistsException
import me.ex1tus.url.shortener.exception.SlugNotFoundException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger { }

@Service
class LinkService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val slugGenerator: SlugGenerator,
    @Value("\${server.url}") private val serverUrl: String
) {

    fun createShortLink(request: ShortLinkRequest): ShortLinkResponse {
        val slug = request.slug
            ?.let { checkForUniqueness(it) }
            ?: generateUniqueSlug()

        log.info { "Saving new slug=$slug for destination=${request.destination} to Redis" }
        redisTemplate.opsForValue().set(slug, request.destination)

        return ShortLinkResponse(request.destination, "$serverUrl/$slug")
    }

    @Cacheable("links")
    fun getLongLink(slug: String): String {
        log.info { "Looking for original link in Redis with slug=$slug" }
        return redisTemplate.opsForValue().get(slug) ?: throw SlugNotFoundException(slug)
    }

    private fun checkForUniqueness(slug: String): String {
        if (redisTemplate.hasKey(slug)) {
            log.warn { "Slug=$slug already exists" }
            throw SlugAlreadyExistsException(slug)
        }

        return slug
    }

    private fun generateUniqueSlug(): String {
        var attempts = 0
        while (true) {
            attempts++
            val slug = slugGenerator.generate()
            if (!redisTemplate.hasKey(slug)) {
                return slug
                    .also { log.info { "Generated new slug=$slug in $attempts attempt(s)" } }
            }
        }
    }
}
