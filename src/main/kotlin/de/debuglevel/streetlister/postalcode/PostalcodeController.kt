package de.debuglevel.streetlister.postalcode

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.server.types.files.StreamedFile
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import java.util.*

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/postalcodes")
@Tag(name = "postalcodes")
class PostalcodeController(private val postalcodeService: PostalcodeService) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get all postalcodes
     * @return All postalcodes
     */
    @Get("/")
    fun getAllPostalcodes(): HttpResponse<List<GetPostalcodeResponse>> {
        logger.debug("Called getAllPostalcodes()")
        return try {
            val postalcodes = postalcodeService.list()
            val getPostalcodeResponses = postalcodes
                .map { GetPostalcodeResponse(it) }

            HttpResponse.ok(getPostalcodeResponses)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Get a postalcode
     * @param id ID of the postalcode
     * @return A postalcode
     */
    @Get("/{id}")
    fun getOnePostalcode(id: UUID): HttpResponse<GetPostalcodeResponse> {
        logger.debug("Called getOnePostalcode($id)")
        return try {
            val postalcode = postalcodeService.get(id)

            val getPostalcodeResponse = GetPostalcodeResponse(postalcode)
            HttpResponse.ok(getPostalcodeResponse)
        } catch (e: PostalcodeService.EntityNotFoundException) {
            logger.debug { "Getting postalcode $id failed: ${e.message}" }
            HttpResponse.notFound()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Delete a postalcode.
     * @param id ID of the postalcode
     */
    @Delete("/{id}")
    fun deleteOnePostalcode(id: UUID): HttpResponse<Unit> {
        logger.debug("Called deleteOnePostalcode($id)")
        return try {
            postalcodeService.delete(id)

            HttpResponse.noContent()
        } catch (e: PostalcodeService.EntityNotFoundException) {
            HttpResponse.notFound()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Delete all postalcode.
     */
    @Delete("/")
    fun deleteAllPostalcodes(): HttpResponse<Unit> {
        logger.debug("Called deleteAllPostalcodes()")
        return try {
            postalcodeService.deleteAll()

            HttpResponse.noContent()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }
}