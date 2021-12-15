package de.debuglevel.streetdirectory.street

import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import java.time.LocalDateTime
import java.util.*

data class GetStreetResponse(
    val id: UUID,
    /**
     * The postal code
     */
    var postalcode: String,
    /**
     * The name of the street
     */
    var streetname: String,
    /**
     * Latitude of the center of the postal code area
     */
    var centerLatitude: Double?,
    /**
     * Longitude of the center of the postal code area
     */
    var centerLongitude: Double?,
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
    constructor(street: Street) : this(
        street.id!!,
        street.postalcode.code,
        street.streetname,
        street.centerLatitude,
        street.centerLongitude,
        street.createdOn,
        street.lastModifiedOn
    )
}