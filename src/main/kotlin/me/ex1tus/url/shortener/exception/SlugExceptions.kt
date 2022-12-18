package me.ex1tus.url.shortener.exception

class SlugAlreadyExistsException(slug: String) : RuntimeException(slug)

class SlugNotFoundException(slug: String) : RuntimeException(slug)
