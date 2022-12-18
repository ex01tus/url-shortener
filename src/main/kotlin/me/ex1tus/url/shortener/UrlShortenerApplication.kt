package me.ex1tus.url.shortener

import me.ex1tus.url.shortener.config.property.CaffeineProperties
import me.ex1tus.url.shortener.config.property.SlugProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(value = [SlugProperties::class, CaffeineProperties::class])
class UrlShortenerApplication

fun main(args: Array<String>) {
    runApplication<UrlShortenerApplication>(*args)
}
