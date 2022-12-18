package me.ex1tus.url.shortener.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import me.ex1tus.url.shortener.exception.SlugNotFoundException
import me.ex1tus.url.shortener.service.LinkService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

private val log = KotlinLogging.logger { }

@RestController
class RedirectController(
    private val service: LinkService
) {

    @GetMapping("/{slug}")
    @Operation(summary = "Redirect to the original long link")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "302", description = "Redirected", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Slug not found")
        ]
    )
    fun redirect(@PathVariable slug: String) = ResponseEntity
        .status(HttpStatus.FOUND)
        .location(URI.create(service.getLongLink(slug)))
        .build<Unit>()
        .also { log.info { "Redirected /$slug to ${it.headers.location}" } }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SlugNotFoundException::class)
    fun handleSlugNotFoundException() = log.warn { "<- [POST /link/short] 404 (NOT FOUND)" }
}
