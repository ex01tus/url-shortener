package me.ex1tus.url.shortener.domain


data class ShortLinkRequest(
    val destination: String,
    val slug: String?
)
