package de.debuglevel.streetlister.street

data class AddStreetRequest(
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
) {
    fun toStreet(): Street {
        return Street(
            id = null,
            postalcode = postalcode,
            streetname = streetname,
            centerLatitude = centerLatitude,
            centerLongitude = centerLongitude,
        )
    }
}