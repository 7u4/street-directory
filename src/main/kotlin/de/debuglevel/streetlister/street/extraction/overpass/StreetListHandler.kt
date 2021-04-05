package de.debuglevel.streetlister.street.extraction.overpass

import de.debuglevel.openstreetmap.overpass.EmptyResultSetException
import de.debuglevel.openstreetmap.overpass.InvalidResultSetException
import de.debuglevel.openstreetmap.overpass.OverpassResultHandler
import de.debuglevel.streetlister.street.Street
import de.westnordost.osmapi.common.ListHandler
import mu.KotlinLogging

class StreetListHandler(
    /**
     * Postal code that will be assigned to every street by this Handler
     */
    private val postalcode: String
) : ListHandler<Array<String>>(), OverpassResultHandler<Street> {
    private val logger = KotlinLogging.logger {}

    override fun getResults(): List<Street> {
        logger.debug { "Getting streets..." }

        val stringArrays = this.get()

        if (stringArrays.count() == 0) {
            // first line should be CSV header; but not even this line is present
            throw InvalidResultSetException()
        } else if (stringArrays.count() == 1) {
            // a first line is present, but no further lines
            throw EmptyResultSetException()
        }

        val streets = stringArrays
            .drop(1) // skip header
            .map {
                logger.trace { "Parsing '${it.joinToString("|")}' ..." }
                val id = it[0].toLongOrNull()
                val type = it[1].ifBlank { null }
                val typeId = it[2].toIntOrNull()
                val latitude = it[3].toDoubleOrNull()
                val longitude = it[4].toDoubleOrNull()
                val streetname = it[5].ifBlank { throw BlankStreetnameException(id) }

                Street(
                    id = null,
                    postalcode = postalcode,
                    streetname = streetname,
                    centerLatitude = latitude,
                    centerLongitude = longitude,
                )
            }
        logger.debug { "Got ${streets.count()} streets" }
        return streets
    }

    data class BlankStreetnameException(val id: Long?) : Exception("id=$id has an empty streetname")
}