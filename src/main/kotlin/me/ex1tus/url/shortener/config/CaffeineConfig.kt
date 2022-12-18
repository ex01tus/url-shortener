package me.ex1tus.url.shortener.config

import com.github.benmanes.caffeine.cache.Caffeine
import me.ex1tus.url.shortener.config.property.CaffeineProperties
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CaffeineConfig(
    private val caffeineProperties: CaffeineProperties
) {

    @Bean
    fun cacheManager() = CaffeineCacheManager(caffeineProperties.cacheName).apply {
        setCaffeine(caffeineCacheBuilder())
    }

    private fun caffeineCacheBuilder() = Caffeine.newBuilder()
        .initialCapacity(caffeineProperties.initialCapacity)
        .maximumSize(caffeineProperties.maximumSize)
        .expireAfterAccess(caffeineProperties.expireAfterAccessSeconds, TimeUnit.SECONDS)
}
