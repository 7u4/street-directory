package de.debuglevel.streetlister.postalcode

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

    )