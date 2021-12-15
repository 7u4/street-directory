package de.debuglevel.streetdirectory.postalcode

import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import java.time.LocalDateTime
import java.util.*

data class GetPostalcodeResponse(
    val id: UUID,
    /**
     * The actual value of the postal code
     */
    var code: String,
    /**
     * Number of streets associated with this postal code
     */
    var streetsCount: Int,
    /**
     * Latitude of the center of the postal code area
     */
    var centerLatitude: Double?,
    /**
     * Longitude of the center of the postal code area
     */
    var centerLongitude: Double?,
    /**
     * A note which might be available from some providers
     */
    var note: String?,
    /**
     * DateTime when the last attempt to street extraction was made; null if not yet attempted.
     */
    var lastStreetExtractionOn: LocalDateTime? = null,
    /**
     * When created in the database
     */
    @DateCreated
    var createdOn: LocalDateTime,
    /**
     * When last modified in the database
     */
    @DateUpdated
    var lastModifiedOn: LocalDateTime,
) {
    constructor(postalcode: Postalcode) : this(
        postalcode.id!!,
        postalcode.code,
        postalcode.streets.size,
        postalcode.centerLatitude,
        postalcode.centerLongitude,
        postalcode.note,
        postalcode.lastStreetExtractionOn,
        postalcode.createdOn,
        postalcode.lastModifiedOn
    )
}