package de.debuglevel.streetlister.street.extraction

import de.debuglevel.streetlister.postalcode.Postalcode

data class OverpassStreetExtractorSettings(
    val areaId: Long,
    val postalcode: Postalcode,
) : StreetExtractorSettings