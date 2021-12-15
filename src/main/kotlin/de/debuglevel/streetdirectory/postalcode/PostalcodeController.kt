package de.debuglevel.streetdirectory.postalcode

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import java.util.*
import javax.transaction.Transactional

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/postalcodes")
@Tag(name = "postalcodes")
open class PostalcodeController(
    private val postalcodeService: PostalcodeService
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get all [Postalcode]s
     * @return All [Postalcode]s
     */
    @Get("/")
    @Transactional
    open fun getAllPostalcodes(): HttpResponse<List<GetPostalcodeResponse>> {
        logger.debug("Called getAllPostalcodes()")
        return try {
            val postalcodes = postalcodeService.getAll()
            val getPostalcodeResponses = postalcodes
                .map { GetPostalcodeResponse(it) }

            HttpResponse.ok(getPostalcodeResponses)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Get a [Postalcode]
     * @param id ID of the [Postalcode]
     * @return A [Postalcode]
     */
    @Get("/{id}")
    fun getOnePostalcode(id: UUID): HttpResponse<GetPostalcodeResponse> {
        logger.debug("Called getOnePostalcode($id)")
        return try {
            val postalcode = postalcodeService.get(id)

            val getPostalcodeResponse = GetPostalcodeResponse(postalcode)
            HttpResponse.ok(getPostalcodeResponse)
        } catch (e: PostalcodeService.ItemNotFoundException) {
            logger.debug { "Getting postalcode $id failed: ${e.message}" }
            HttpResponse.notFound()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Delete a [Postalcode].
     * @param id ID of the [Postalcode]
     */
    @Delete("/{id}")
    fun deleteOnePostalcode(id: UUID): HttpResponse<Unit> {
        logger.debug("Called deleteOnePostalcode($id)")
        return try {
            postalcodeService.delete(id)

            HttpResponse.noContent()
        } catch (e: PostalcodeService.ItemNotFoundException) {
            HttpResponse.notFound()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Delete all [Postalcode]s.
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

    /**
     * Create a [Postalcode].
     * This is not meant for productive use, but rather for uploading backups.
     */
    @Post("/")
    fun postOnePostalcode(addPostalcodeRequest: AddPostalcodeRequest): HttpResponse<AddPostalcodeResponse> {
        logger.debug("Called postOnePostalcode($addPostalcodeRequest)")

        return try {
            val postalcode = addPostalcodeRequest.toPostalcode()
            val addedPostalcode = postalcodeService.updateOrAdd(postalcode)

            val addPostalcodeResponse = AddPostalcodeResponse(addedPostalcode)
            HttpResponse.created(addPostalcodeResponse)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    //@Post("/populate/{?areaId}") // Does not work, but would be nicer?
    //@Post("/populate{?areaId}") // Does not work, but would be nicer?
    @Post("/populate/{areaId}")
    fun populate(areaId: Long) {
        logger.debug { "Called populate($areaId)" }
        postalcodeService.populate(areaId)
    }
}