package me.ex1tus.url.shortener.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class RandomConfig {

    @Bean
    fun random() = Random()
}
