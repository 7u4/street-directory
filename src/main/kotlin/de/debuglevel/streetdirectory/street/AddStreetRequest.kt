package de.debuglevel.streetdirectory.street

import de.debuglevel.streetdirectory.postalcode.Postalcode

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
    fun toStreet(postalcodeObj: Postalcode): Street {
        return Street(
            id = null,
            postalcode = postalcodeObj,
            streetname = streetname,
            centerLatitude = centerLatitude,
            centerLongitude = centerLongitude,
        )
    }
}