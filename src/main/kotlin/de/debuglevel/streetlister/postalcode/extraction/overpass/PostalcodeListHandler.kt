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
            .drop(1)
            .map {
                //logger.trace { "Parsing ${it[0]} ..." }
                Postalcode(
                    id = null,
                    code = it[0]
                )
            }
        logger.debug { "Got ${postalcodes.count()} postal codes" }
        return postalcodes
    }
}