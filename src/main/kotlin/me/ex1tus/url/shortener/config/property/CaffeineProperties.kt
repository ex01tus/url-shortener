package me.ex1tus.url.shortener.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("caffeine")
data class CaffeineProperties(
    val cacheName: String,
    val initialCapacity: Int,
    val maximumSize: Long,
    val expireAfterAccessSeconds: Long
)
