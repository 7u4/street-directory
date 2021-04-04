package de.debuglevel.streetlister.postalcode

import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Postalcode(
    @Id
    @GeneratedValue
    var id: UUID?,
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
    var note: String? = null,
    /**
     * DateTime when the last attempt to street extraction was made; null if not yet attempted.
     */
    var lastStreetExtractionOn: LocalDateTime? = null,
    /**
     * When created in the database
     */
    @DateCreated
    var createdOn: LocalDateTime = LocalDateTime.now(),
    /**
     * When last modified in the database
     */
    @DateUpdated
    var lastModifiedOn: LocalDateTime = LocalDateTime.now(),
)