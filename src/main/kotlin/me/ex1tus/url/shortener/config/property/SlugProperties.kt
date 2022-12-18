package me.ex1tus.url.shortener.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("slug")
data class SlugProperties(
    val length: Int,
    val alphabet: String
)
