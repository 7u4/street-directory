package de.debuglevel.streetlister.postalcode.extraction.overpass

import de.debuglevel.streetlister.postalcode.Postalcode
import de.westnordost.osmapi.common.ListHandler
import mu.KotlinLogging

class PostalcodeListHandler : ListHandler<Array<String>>() {
    private val logger = KotlinLogging.logger {}

    fun getPostalcodes(): List<Postalcode> {
        logger.debug { "Getting postal codes..." }

        val stringArrays = this.get()

        if (stringArrays.count() == 0) {
            // first line should be CSV header; but not even this line is present
            throw InvalidResultSetException()
        } else if (stringArrays.count() == 1) {
            // a first line is present, but no further lines
            throw EmptyResultSetException()
        }

        val postalcodes = stringArrays
            .drop(1) // skip header
            .map {
                logger.trace { "Parsing '${it.joinToString("|")}' ..." }
                val id = it[0].toLongOrNull()
                val type = it[1].ifBlank { null }
                val typeId = it[2].toIntOrNull()
                val latitude = it[3].toDoubleOrNull()
                val longitude = it[4].toDoubleOrNull()
                val code = it[5].ifBlank { throw BlankCodeException(id) }
                val note = it.elementAtOrNull(6)?.ifBlank { null }

                Postalcode(
                    id = null,
                    code = code,
                    centerLatitude = latitude,
                    centerLongitude = longitude,
                    note = note
                )
            }
        logger.debug { "Got ${postalcodes.count()} postal codes" }
        return postalcodes
    }

    class InvalidResultSetException : Exception("Received ResultSet is invalid")
    class EmptyResultSetException : Exception("Received ResultSet is empty")
    data class BlankCodeException(val id: Long?) : Exception("id=$id has an empty code")
}