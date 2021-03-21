package de.debuglevel.streetlister.postalcode.extraction.overpass

import de.debuglevel.streetlister.postalcode.Postalcode
import de.westnordost.osmapi.common.ListHandler
import mu.KotlinLogging

class PostalcodeListHandler : ListHandler<Array<String>>() {
    private val logger = KotlinLogging.logger {}

    fun getPostalcodes(): List<Postalcode> {
        logger.debug { "Getting postal codes..." }

        val stringArrays = this.get()
        val postalcodes = stringArrays
            .drop(1) // skip header
            .map {
                //logger.trace { "Parsing ${it[0]} ..." }
                val id = it[0].toLongOrNull()
                val type = it[1]
                val typeId = it[2].toIntOrNull()
                val latitude = it[3].toDoubleOrNull()
                val longitude = it[4].toDoubleOrNull()
                val code = it[5]
                val note = it[6]

                Postalcode(
                    id = null,
                    code = code
                )
            }
        logger.debug { "Got ${postalcodes.count()} postal codes" }
        return postalcodes
    }
}