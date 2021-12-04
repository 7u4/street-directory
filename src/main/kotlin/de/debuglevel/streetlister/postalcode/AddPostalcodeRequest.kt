package de.debuglevel.streetlister.postalcode

import java.time.LocalDateTime

data class AddPostalcodeRequest(
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
     * DateTime when the last attempt to street extraction was made; null if not yet attempted.
     */
    var lastStreetExtractionOn: LocalDateTime? = null,
) {
    fun toPostalcode(): Postalcode {
        return Postalcode(
            id = null,
            code = code,
            streets = listOf(),
            centerLatitude = centerLatitude,
            centerLongitude = centerLongitude,
            note = note,
            lastStreetExtractionOn = lastStreetExtractionOn,
        )
    }
}