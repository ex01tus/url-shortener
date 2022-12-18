package me.ex1tus.url.shortener.service

import me.ex1tus.url.shortener.config.property.SlugProperties
import org.springframework.stereotype.Service
import java.util.*

@Service
class SlugGenerator(
    private val slugProperties: SlugProperties,
    private val random: Random
) {

    fun generate(): String {
        val result = CharArray(slugProperties.length)
        for (i in (0 until slugProperties.length)) {
            val randomIndex = random.nextInt(slugProperties.alphabet.length - 1)
            result[i] = slugProperties.alphabet[randomIndex]
        }

        return String(result)
    }
}
