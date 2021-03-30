package de.debuglevel.streetlister.postalcode

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
) {
    fun toPostalcode(): Postalcode {
        return Postalcode(
            id = null,
            code = code,
            centerLatitude = centerLatitude,
            centerLongitude = centerLongitude,
            note = note,
        )
    }
}