package me.ex1tus.url.shortener.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import me.ex1tus.url.shortener.domain.ShortLinkRequest
import me.ex1tus.url.shortener.domain.ShortLinkResponse
import me.ex1tus.url.shortener.exception.SlugAlreadyExistsException
import me.ex1tus.url.shortener.service.LinkService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger { }

@RestController
@RequestMapping("/api/v1/link")
class LinkController(
    private val service: LinkService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/short", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Create a new short link")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Short link created"),
            ApiResponse(responseCode = "400", description = "Slug already exists")
        ]
    )
    fun createShortLink(@RequestBody request: ShortLinkRequest): ShortLinkResponse {
        log.info { "-> [POST /link/short] $request" }
        return service.createShortLink(request)
            .also { log.info { "<- [POST /link/short] $it" } }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SlugAlreadyExistsException::class)
    fun handleSlugAlreadyExistsException() = log.warn { "<- [POST /link/short] 400 (BAD REQUEST)" }
}
