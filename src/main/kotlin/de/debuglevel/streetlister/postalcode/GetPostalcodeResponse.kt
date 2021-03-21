package de.debuglevel.streetlister.postalcode

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
        postalcode.centerLatitude,
        postalcode.centerLongitude,
        postalcode.note,
        postalcode.createdOn,
        postalcode.lastModifiedOn
    )
}